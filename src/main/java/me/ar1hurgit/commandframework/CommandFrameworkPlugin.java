package me.ar1hurgit.commandframework;

import me.ar1hurgit.commandframework.demo.command.BanCommand;
import me.ar1hurgit.commandframework.demo.command.BanHistoryCommand;
import me.ar1hurgit.commandframework.demo.command.PingCommand;
import me.ar1hurgit.commandframework.framework.context.CommandContext;
import me.ar1hurgit.commandframework.framework.core.CommandFramework;
import me.ar1hurgit.commandframework.framework.core.DefaultMessageResolver;
import me.ar1hurgit.commandframework.framework.validation.CommandMiddleware;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandFrameworkPlugin extends JavaPlugin {

    private CommandFramework commandFramework;

    @Override
    public void onEnable() {
        commandFramework = CommandFramework.builder(this)
            .messages(new DefaultMessageResolver())
            .middleware(new LoggingMiddleware())
            .build();

        commandFramework.register(new BanCommand());
        commandFramework.register(new BanHistoryCommand());
        commandFramework.register(new PingCommand());
        commandFramework.scanAndRegister("me.ar1hurgit.commandframework.demo.scanned");

        getLogger().info("Command Framework demo enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Command Framework demo disabled.");
    }

    private final class LoggingMiddleware implements CommandMiddleware {

        @Override
        public void beforeExecute(CommandContext context) {
            getLogger().info("Executing /" + context.definition().commandKey() + " for " + context.sender().getName());
        }

        @Override
        public void onError(CommandContext context, Throwable throwable) {
            getLogger().warning("Command failure on /" + context.definition().commandKey() + ": " + throwable.getMessage());
        }
    }
}
