package me.ar1hurgit.commandframework.framework.context;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ar1hurgit.commandframework.framework.core.CommandDefinition;
import me.ar1hurgit.commandframework.framework.core.CommandFramework;
import me.ar1hurgit.commandframework.framework.core.MessageKey;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandContext {

    private final JavaPlugin plugin;
    private final CommandFramework framework;
    private final CommandSender sender;
    private final String label;
    private final List<String> rawArguments;
    private final List<String> invocationArguments;
    private final CommandDefinition definition;
    private final long startedAtNanos;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private volatile List<Object> parsedArguments = List.of();

    public CommandContext(
        JavaPlugin plugin,
        CommandFramework framework,
        CommandSender sender,
        String label,
        List<String> rawArguments,
        List<String> invocationArguments,
        CommandDefinition definition
    ) {
        this.plugin = plugin;
        this.framework = framework;
        this.sender = sender;
        this.label = label;
        this.rawArguments = List.copyOf(rawArguments);
        this.invocationArguments = List.copyOf(invocationArguments);
        this.definition = definition;
        this.startedAtNanos = System.nanoTime();
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public CommandFramework framework() {
        return framework;
    }

    public CommandSender sender() {
        return sender;
    }

    public String label() {
        return label;
    }

    public List<String> rawArguments() {
        return rawArguments;
    }

    public List<String> invocationArguments() {
        return invocationArguments;
    }

    public CommandDefinition definition() {
        return definition;
    }

    public long startedAtNanos() {
        return startedAtNanos;
    }

    public void setParsedArguments(List<Object> parsedArguments) {
        this.parsedArguments = List.copyOf(parsedArguments);
    }

    public List<Object> parsedArguments() {
        return parsedArguments;
    }

    public Map<String, Object> attributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void putAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object attribute(String key) {
        return attributes.get(key);
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player player() {
        return isPlayer() ? (Player) sender : null;
    }

    public void reply(String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void reply(MessageKey key, Map<String, String> placeholders) {
        reply(framework.messageResolver().resolve(key, placeholders));
    }
}
