package me.ar1hurgit.commandframework.framework.parser;

import java.util.UUID;

import me.ar1hurgit.commandframework.framework.exception.PlayerNotFoundException;
import org.bukkit.OfflinePlayer;

import static org.bukkit.Bukkit.getOfflinePlayer;

public final class OfflinePlayerArgumentParser implements ArgumentParser<OfflinePlayer> {

    @Override
    public OfflinePlayer parse(ParameterParseContext context) {
        try {
            UUID uniqueId = UUID.fromString(context.input());
            return getOfflinePlayer(uniqueId);
        } catch (IllegalArgumentException ignored) {
            // Falls back to name resolution.
        }

        OfflinePlayer player = getOfflinePlayer(context.input());
        if (player == null || (!player.hasPlayedBefore() && !player.isOnline())) {
            throw new PlayerNotFoundException("Unknown offline player: " + context.input());
        }
        return player;
    }
}
