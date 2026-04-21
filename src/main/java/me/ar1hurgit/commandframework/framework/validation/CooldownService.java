package me.ar1hurgit.commandframework.framework.validation;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ar1hurgit.commandframework.framework.context.CommandContext;
import me.ar1hurgit.commandframework.framework.core.CommandDefinition;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CooldownService {

    private final Map<String, Instant> cooldowns = new ConcurrentHashMap<>();

    public Duration remaining(CommandSender sender, CommandDefinition definition) {
        if (definition.cooldownSeconds() <= 0L) {
            return Duration.ZERO;
        }

        Instant expiry = cooldowns.get(key(sender, definition));
        if (expiry == null) {
            return Duration.ZERO;
        }

        Instant now = Instant.now();
        return expiry.isAfter(now) ? Duration.between(now, expiry) : Duration.ZERO;
    }

    public void stamp(CommandContext context) {
        CommandDefinition definition = context.definition();
        if (definition.cooldownSeconds() <= 0L) {
            return;
        }

        cooldowns.put(key(context.sender(), definition), Instant.now().plusSeconds(definition.cooldownSeconds()));
    }

    private String key(CommandSender sender, CommandDefinition definition) {
        String subject = sender instanceof Player player
            ? player.getUniqueId().toString()
            : sender.getName().toLowerCase();
        return subject + ":" + definition.commandKey();
    }
}
