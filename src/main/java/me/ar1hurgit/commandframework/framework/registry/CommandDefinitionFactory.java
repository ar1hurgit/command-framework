package me.ar1hurgit.commandframework.framework.registry;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import me.ar1hurgit.commandframework.framework.annotation.Alias;
import me.ar1hurgit.commandframework.framework.annotation.Async;
import me.ar1hurgit.commandframework.framework.annotation.Command;
import me.ar1hurgit.commandframework.framework.annotation.Cooldown;
import me.ar1hurgit.commandframework.framework.annotation.Description;
import me.ar1hurgit.commandframework.framework.annotation.Optional;
import me.ar1hurgit.commandframework.framework.annotation.Permission;
import me.ar1hurgit.commandframework.framework.annotation.Range;
import me.ar1hurgit.commandframework.framework.annotation.Suggest;
import me.ar1hurgit.commandframework.framework.annotation.Usage;
import me.ar1hurgit.commandframework.framework.context.CommandContext;
import me.ar1hurgit.commandframework.framework.core.CommandDefinition;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;
import me.ar1hurgit.commandframework.framework.exception.CommandDefinitionException;
import me.ar1hurgit.commandframework.framework.util.ReflectionUtils;
import me.ar1hurgit.commandframework.framework.util.StringUtils;
import org.bukkit.command.CommandSender;

public final class CommandDefinitionFactory {

    public CommandDefinition create(Object instance) {
        Class<?> type = instance.getClass();
        Command command = type.getAnnotation(Command.class);
        if (command == null) {
            throw new CommandDefinitionException(type.getSimpleName() + " is missing @Command.");
        }

        Method method = ReflectionUtils.findExecuteMethod(type);
        List<String> fullPath = StringUtils.splitCommandPath(command.value());
        if (fullPath.isEmpty()) {
            throw new CommandDefinitionException("@Command value cannot be empty for " + type.getSimpleName() + ".");
        }

        List<CommandParameterDescriptor> parameters = resolveParameters(method);
        validateParameterOrder(type, parameters);

        return new CommandDefinition(
            instance,
            method,
            ReflectionUtils.bindHandle(instance, method),
            fullPath,
            resolveRoutePaths(type, fullPath),
            resolveRootAliases(type, fullPath),
            parameters,
            resolveDescription(type, method),
            resolveUsage(type, method),
            resolvePermission(type, method),
            resolveCooldown(type, method),
            resolveAsync(type, method)
        );
    }

    private List<List<String>> resolveRoutePaths(Class<?> type, List<String> fullPath) {
        List<List<String>> routePaths = new ArrayList<>();
        routePaths.add(List.copyOf(fullPath.subList(1, fullPath.size())));

        Alias alias = type.getAnnotation(Alias.class);
        if (alias == null) {
            return routePaths;
        }

        for (String aliasValue : alias.value()) {
            List<String> aliasTokens = StringUtils.splitCommandPath(aliasValue);
            if (aliasTokens.isEmpty()) {
                continue;
            }
            if (fullPath.size() == 1 && aliasTokens.size() == 1) {
                continue;
            }
            if (aliasTokens.size() == 1) {
                List<String> aliasPath = new ArrayList<>(fullPath.subList(1, fullPath.size()));
                if (aliasPath.isEmpty()) {
                    continue;
                }
                aliasPath.set(aliasPath.size() - 1, aliasTokens.get(0));
                routePaths.add(List.copyOf(aliasPath));
                continue;
            }
            if (!aliasTokens.get(0).equalsIgnoreCase(fullPath.get(0))) {
                throw new CommandDefinitionException("Alias '" + aliasValue + "' must share the root '" + fullPath.get(0) + "'.");
            }
            routePaths.add(List.copyOf(aliasTokens.subList(1, aliasTokens.size())));
        }

        return routePaths;
    }

    private Set<String> resolveRootAliases(Class<?> type, List<String> fullPath) {
        Set<String> aliases = new LinkedHashSet<>();
        Alias alias = type.getAnnotation(Alias.class);
        if (alias == null) {
            return aliases;
        }

        for (String aliasValue : alias.value()) {
            List<String> aliasTokens = StringUtils.splitCommandPath(aliasValue);
            if (aliasTokens.isEmpty()) {
                continue;
            }
            if (aliasTokens.size() == 1 && fullPath.size() == 1) {
                aliases.add(aliasTokens.get(0));
                continue;
            }
            if (aliasTokens.size() == fullPath.size()) {
                aliases.add(aliasTokens.get(0));
            }
        }

        return aliases;
    }

    private List<CommandParameterDescriptor> resolveParameters(Method method) {
        List<CommandParameterDescriptor> parameters = new ArrayList<>();
        Parameter[] methodParameters = method.getParameters();
        for (int index = 0; index < methodParameters.length; index++) {
            Parameter parameter = methodParameters[index];
            CommandParameterDescriptor.ParameterKind kind = resolveKind(parameter);
            Range range = parameter.getAnnotation(Range.class);
            Suggest suggest = parameter.getAnnotation(Suggest.class);
            boolean greedy = kind == CommandParameterDescriptor.ParameterKind.ARGUMENT
                && index == methodParameters.length - 1
                && parameter.getType() == String.class;

            parameters.add(new CommandParameterDescriptor(
                index,
                kind,
                parameter.getName(),
                parameter.getType(),
                parameter.isAnnotationPresent(Optional.class),
                range == null ? null : range.min(),
                range == null ? null : range.max(),
                suggest == null ? List.of() : List.of(suggest.value()),
                greedy
            ));
        }
        return parameters;
    }

    private CommandParameterDescriptor.ParameterKind resolveKind(Parameter parameter) {
        if (CommandContext.class.isAssignableFrom(parameter.getType())) {
            return CommandParameterDescriptor.ParameterKind.CONTEXT;
        }
        if (CommandSender.class.isAssignableFrom(parameter.getType())) {
            return CommandParameterDescriptor.ParameterKind.SENDER;
        }
        return CommandParameterDescriptor.ParameterKind.ARGUMENT;
    }

    private void validateParameterOrder(Class<?> type, List<CommandParameterDescriptor> parameters) {
        boolean optionalSeen = false;
        for (int index = 0; index < parameters.size(); index++) {
            CommandParameterDescriptor parameter = parameters.get(index);
            if (parameter.kind() != CommandParameterDescriptor.ParameterKind.ARGUMENT) {
                continue;
            }
            if (parameter.optional()) {
                optionalSeen = true;
            } else if (optionalSeen) {
                throw new CommandDefinitionException("Required argument '" + parameter.name() + "' cannot appear after an optional argument in " + type.getSimpleName() + ".");
            }
            if (parameter.greedy() && index != parameters.size() - 1) {
                throw new CommandDefinitionException("Greedy String arguments must be the last parameter in " + type.getSimpleName() + ".");
            }
        }
    }

    private String resolveDescription(Class<?> type, Method method) {
        Description description = method.getAnnotation(Description.class);
        if (description == null) {
            description = type.getAnnotation(Description.class);
        }
        return description == null ? null : description.value();
    }

    private String resolveUsage(Class<?> type, Method method) {
        Usage usage = method.getAnnotation(Usage.class);
        if (usage == null) {
            usage = type.getAnnotation(Usage.class);
        }
        return usage == null ? null : usage.value();
    }

    private String resolvePermission(Class<?> type, Method method) {
        Permission permission = method.getAnnotation(Permission.class);
        if (permission == null) {
            permission = type.getAnnotation(Permission.class);
        }
        return permission == null ? null : permission.value();
    }

    private long resolveCooldown(Class<?> type, Method method) {
        Cooldown cooldown = method.getAnnotation(Cooldown.class);
        if (cooldown == null) {
            cooldown = type.getAnnotation(Cooldown.class);
        }
        return cooldown == null ? 0L : cooldown.value();
    }

    private boolean resolveAsync(Class<?> type, Method method) {
        return type.isAnnotationPresent(Async.class) || method.isAnnotationPresent(Async.class);
    }
}
