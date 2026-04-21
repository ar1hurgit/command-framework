package me.ar1hurgit.commandframework.framework.core;

import java.util.ArrayList;
import java.util.List;

import me.ar1hurgit.commandframework.framework.completion.CompletionProvider;
import me.ar1hurgit.commandframework.framework.completion.CompletionRegistry;
import me.ar1hurgit.commandframework.framework.executor.CommandErrorHandler;
import me.ar1hurgit.commandframework.framework.executor.FrameworkCommandExecutor;
import me.ar1hurgit.commandframework.framework.help.HelpService;
import me.ar1hurgit.commandframework.framework.parser.ArgumentParser;
import me.ar1hurgit.commandframework.framework.parser.ParserRegistry;
import me.ar1hurgit.commandframework.framework.registry.BukkitCommandRegistrar;
import me.ar1hurgit.commandframework.framework.registry.CommandClassScanner;
import me.ar1hurgit.commandframework.framework.registry.CommandDefinitionFactory;
import me.ar1hurgit.commandframework.framework.registry.CommandRegistry;
import me.ar1hurgit.commandframework.framework.validation.CommandMiddleware;
import me.ar1hurgit.commandframework.framework.validation.CommandValidationService;
import me.ar1hurgit.commandframework.framework.validation.CooldownService;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandFramework {

    private final JavaPlugin plugin;
    private final FrameworkConfiguration configuration;
    private final ParserRegistry parserRegistry;
    private final CompletionRegistry completionRegistry;
    private final CooldownService cooldownService;
    private final CommandMetrics metrics;
    private final List<CommandMiddleware> middlewares;
    private final CommandRegistry commandRegistry;
    private final CommandClassScanner classScanner;

    public CommandFramework(JavaPlugin plugin, FrameworkConfiguration configuration, List<CommandMiddleware> middlewares) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.parserRegistry = new ParserRegistry();
        this.completionRegistry = new CompletionRegistry();
        this.cooldownService = new CooldownService();
        this.metrics = new CommandMetrics();
        this.middlewares = new ArrayList<>(middlewares);

        CommandValidationService validationService = new CommandValidationService(cooldownService);
        FrameworkCommandExecutor executor = new FrameworkCommandExecutor(
            plugin,
            this,
            validationService,
            completionRegistry,
            new HelpService(),
            new CommandErrorHandler()
        );

        this.commandRegistry = new CommandRegistry(new CommandDefinitionFactory(), new BukkitCommandRegistrar(plugin, executor));
        this.classScanner = new CommandClassScanner(plugin.getClass().getClassLoader());
    }

    public static CommandFrameworkBuilder builder(JavaPlugin plugin) {
        return new CommandFrameworkBuilder(plugin);
    }

    public void register(Object commandInstance) {
        commandRegistry.register(commandInstance);
    }

    public int scanAndRegister(String packageName) {
        var discovered = classScanner.scan(packageName);
        discovered.forEach(this::register);
        return discovered.size();
    }

    public <T> void registerParser(Class<T> type, ArgumentParser<? extends T> parser) {
        parserRegistry.register(type, parser);
    }

    public <T> void registerCompletion(Class<T> type, CompletionProvider provider) {
        completionRegistry.register(type, provider);
    }

    public void addMiddleware(CommandMiddleware middleware) {
        middlewares.add(middleware);
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public FrameworkConfiguration configuration() {
        return configuration;
    }

    public MessageResolver messageResolver() {
        return configuration.messageResolver();
    }

    public ParserRegistry parserRegistry() {
        return parserRegistry;
    }

    public CompletionRegistry completionRegistry() {
        return completionRegistry;
    }

    public CooldownService cooldownService() {
        return cooldownService;
    }

    public CommandMetrics metrics() {
        return metrics;
    }

    public List<CommandMiddleware> middlewares() {
        return List.copyOf(middlewares);
    }
}
