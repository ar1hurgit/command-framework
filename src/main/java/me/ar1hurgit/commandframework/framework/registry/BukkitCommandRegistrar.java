package me.ar1hurgit.commandframework.framework.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.ar1hurgit.commandframework.framework.exception.CommandDefinitionException;
import me.ar1hurgit.commandframework.framework.executor.FrameworkCommandExecutor;
import me.ar1hurgit.commandframework.framework.executor.FrameworkPluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitCommandRegistrar {

    private final JavaPlugin plugin;
    private final FrameworkCommandExecutor executor;
    private final SimpleCommandMap commandMap;

    public BukkitCommandRegistrar(JavaPlugin plugin, FrameworkCommandExecutor executor) {
        this.plugin = plugin;
        this.executor = executor;
        this.commandMap = resolveCommandMap(plugin);
    }

    public void register(CommandRoot root) {
        FrameworkPluginCommand command = new FrameworkPluginCommand(root, executor);
        command.setAliases(root.aliases().stream().toList());
        commandMap.register(plugin.getDescription().getName().toLowerCase(), command);
    }

    private SimpleCommandMap resolveCommandMap(JavaPlugin plugin) {
        try {
            Method method = plugin.getServer().getClass().getMethod("getCommandMap");
            method.setAccessible(true);
            return (SimpleCommandMap) method.invoke(plugin.getServer());
        } catch (ReflectiveOperationException ignored) {
            try {
                Field field = plugin.getServer().getClass().getDeclaredField("commandMap");
                field.setAccessible(true);
                return (SimpleCommandMap) field.get(plugin.getServer());
            } catch (ReflectiveOperationException exception) {
                throw new CommandDefinitionException("Unable to access Bukkit command map.", exception);
            }
        }
    }
}
