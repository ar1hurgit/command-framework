package me.ar1hurgit.commandframework.framework.parser;

import me.ar1hurgit.commandframework.framework.exception.PlayerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getPlayerExact;

public final class PlayerArgumentParser implements ArgumentParser<Player> {

    @Override
    public Player parse(ParameterParseContext context) {
        Player player = getPlayerExact(context.input());
        if (player == null) {
            player = getPlayer(context.input());
        }
        if (player == null) {
            throw new PlayerNotFoundException("Unknown player: " + context.input());
        }
        return player;
    }
}
