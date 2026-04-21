package me.ar1hurgit.commandframework.framework.core;

import java.util.ArrayList;
import java.util.List;

import me.ar1hurgit.commandframework.framework.validation.CommandMiddleware;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandFrameworkBuilder {

    private final JavaPlugin plugin;
    private MessageResolver messageResolver = new DefaultMessageResolver();
    private boolean metricsEnabled = true;
    private final List<CommandMiddleware> middlewares = new ArrayList<>();

    public CommandFrameworkBuilder(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public CommandFrameworkBuilder messages(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
        return this;
    }

    public CommandFrameworkBuilder metrics(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
        return this;
    }

    public CommandFrameworkBuilder middleware(CommandMiddleware middleware) {
        this.middlewares.add(middleware);
        return this;
    }

    public CommandFramework build() {
        return new CommandFramework(
            plugin,
            new FrameworkConfiguration(messageResolver, metricsEnabled),
            List.copyOf(middlewares)
        );
    }
}
