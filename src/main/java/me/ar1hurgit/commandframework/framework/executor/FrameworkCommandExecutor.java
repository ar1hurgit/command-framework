package me.ar1hurgit.commandframework.framework.executor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.ar1hurgit.commandframework.framework.completion.CompletionRegistry;
import me.ar1hurgit.commandframework.framework.context.CommandContext;
import me.ar1hurgit.commandframework.framework.context.CompletionContext;
import me.ar1hurgit.commandframework.framework.core.CommandDefinition;
import me.ar1hurgit.commandframework.framework.core.CommandFramework;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;
import me.ar1hurgit.commandframework.framework.core.MessageKey;
import me.ar1hurgit.commandframework.framework.help.HelpService;
import me.ar1hurgit.commandframework.framework.parser.ArgumentParser;
import me.ar1hurgit.commandframework.framework.parser.ParameterParseContext;
import me.ar1hurgit.commandframework.framework.registry.CommandRoot;
import me.ar1hurgit.commandframework.framework.registry.RouteMatch;
import me.ar1hurgit.commandframework.framework.util.StringUtils;
import me.ar1hurgit.commandframework.framework.validation.CommandMiddleware;
import me.ar1hurgit.commandframework.framework.validation.CommandValidationService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class FrameworkCommandExecutor {

    private final JavaPlugin plugin;
    private final CommandFramework framework;
    private final CommandValidationService validationService;
    private final CompletionRegistry completionRegistry;
    private final HelpService helpService;
    private final CommandErrorHandler errorHandler;

    public FrameworkCommandExecutor(
        JavaPlugin plugin,
        CommandFramework framework,
        CommandValidationService validationService,
        CompletionRegistry completionRegistry,
        HelpService helpService,
        CommandErrorHandler errorHandler
    ) {
        this.plugin = plugin;
        this.framework = framework;
        this.validationService = validationService;
        this.completionRegistry = completionRegistry;
        this.helpService = helpService;
        this.errorHandler = errorHandler;
    }

    public boolean execute(CommandSender sender, String label, List<String> arguments, CommandRoot root) {
        if (arguments.size() == 1 && arguments.get(0).equalsIgnoreCase("help")) {
            sendHelp(sender, root, label);
            return true;
        }

        Optional<RouteMatch> match = root.match(arguments);
        if (match.isEmpty()) {
            if (root.hasSubcommands()) {
                sender.sendMessage(framework.messageResolver().resolve(MessageKey.UNKNOWN_SUBCOMMAND, Map.of("command", root.name())));
                return true;
            }
            sendHelp(sender, root, label);
            return true;
        }

        RouteMatch routeMatch = match.get();
        CommandContext context = new CommandContext(
            plugin,
            framework,
            sender,
            label,
            arguments,
            routeMatch.remainingArguments(),
            routeMatch.definition()
        );

        try {
            validationService.validateBeforeParsing(context);
            List<Object> invocationArguments = buildInvocationArguments(context);
            context.setParsedArguments(invocationArguments);

            Runnable invocation = () -> invokeWithPipeline(context, invocationArguments);
            if (routeMatch.definition().async()) {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, invocation);
            } else {
                invocation.run();
            }
        } catch (Throwable throwable) {
            notifyError(context, throwable);
        }

        return true;
    }

    public List<String> complete(CommandSender sender, String label, List<String> arguments, CommandRoot root) {
        String currentInput = arguments.isEmpty() ? "" : arguments.get(arguments.size() - 1);
        List<String> committedArguments = arguments.isEmpty() ? List.of() : arguments.subList(0, arguments.size() - 1);

        List<String> literalSuggestions = root.suggestLiteral(committedArguments, currentInput);
        if (!literalSuggestions.isEmpty()) {
            return literalSuggestions;
        }

        Optional<RouteMatch> match = root.matchForCompletion(committedArguments);
        if (match.isEmpty()) {
            return List.of();
        }

        CommandDefinition definition = match.get().definition();
        List<CommandParameterDescriptor> parameters = definition.argumentParameters();
        int parameterIndex = committedArguments.size() - match.get().matchedPath().size();
        if (parameterIndex < 0 || parameterIndex >= parameters.size()) {
            return List.of();
        }

        CompletionContext context = new CompletionContext(framework, sender, definition, arguments, committedArguments, currentInput);
        return completionRegistry.complete(context, parameters.get(parameterIndex), currentInput);
    }

    private List<Object> buildInvocationArguments(CommandContext context) {
        List<Object> invocationArguments = new ArrayList<>();
        List<Object> parsedCommandArguments = new ArrayList<>();
        List<String> inputs = context.invocationArguments();
        int inputIndex = 0;

        for (CommandParameterDescriptor parameter : context.definition().parameters()) {
            switch (parameter.kind()) {
                case SENDER -> invocationArguments.add(context.sender());
                case CONTEXT -> invocationArguments.add(context);
                case ARGUMENT -> {
                    if (inputIndex >= inputs.size()) {
                        Object defaultValue = parameter.defaultValue();
                        invocationArguments.add(defaultValue);
                        parsedCommandArguments.add(defaultValue);
                        continue;
                    }

                    String rawInput = parameter.greedy()
                        ? StringUtils.joinTail(inputs, inputIndex)
                        : inputs.get(inputIndex);
                    ArgumentParser<?> parser = framework.parserRegistry().resolve(parameter.type());
                    Object parsed = parser.parse(new ParameterParseContext(context, parameter, rawInput, List.copyOf(parsedCommandArguments), inputs.subList(inputIndex, inputs.size())));
                    validationService.validateRange(parameter, parsed);
                    invocationArguments.add(parsed);
                    parsedCommandArguments.add(parsed);

                    if (parameter.greedy()) {
                        inputIndex = inputs.size();
                    } else {
                        inputIndex++;
                    }
                }
            }
        }
        return invocationArguments;
    }

    private void invokeWithPipeline(CommandContext context, List<Object> invocationArguments) {
        List<CommandMiddleware> middlewares = framework.middlewares();
        Duration duration;

        try {
            for (CommandMiddleware middleware : middlewares) {
                middleware.beforeExecute(context);
            }

            context.definition().invoke(invocationArguments);

            for (CommandMiddleware middleware : middlewares) {
                middleware.afterExecute(context);
            }

            framework.cooldownService().stamp(context);
            duration = Duration.ofNanos(System.nanoTime() - context.startedAtNanos());
            if (framework.configuration().metricsEnabled()) {
                framework.metrics().recordSuccess(context.definition().commandKey(), duration);
            }
        } catch (Throwable throwable) {
            duration = Duration.ofNanos(System.nanoTime() - context.startedAtNanos());
            if (framework.configuration().metricsEnabled()) {
                framework.metrics().recordFailure(context.definition().commandKey(), duration);
            }
            notifyError(context, throwable);
        }
    }

    private void notifyError(CommandContext context, Throwable throwable) {
        for (CommandMiddleware middleware : framework.middlewares()) {
            middleware.onError(context, throwable);
        }
        errorHandler.handle(context, throwable);
    }

    private void sendHelp(CommandSender sender, CommandRoot root, String label) {
        CommandContext helpContext = new CommandContext(plugin, framework, sender, label, List.of("help"), List.of("help"), root.routes().get(0));
        helpService.sendRootHelp(sender, root, helpContext);
    }
}
