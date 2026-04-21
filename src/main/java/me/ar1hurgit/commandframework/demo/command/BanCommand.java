package me.ar1hurgit.commandframework.demo.command;

import me.ar1hurgit.commandframework.framework.annotation.Command;
import me.ar1hurgit.commandframework.framework.annotation.Cooldown;
import me.ar1hurgit.commandframework.framework.annotation.Description;
import me.ar1hurgit.commandframework.framework.annotation.Execute;
import me.ar1hurgit.commandframework.framework.annotation.Permission;
import me.ar1hurgit.commandframework.framework.annotation.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("ban")
@Permission("staff.ban")
@Description("Ban a player with an optional reason.")
@Usage("/ban <player> <reason>")
@Cooldown(5)
public final class BanCommand {

    @Execute
    public void execute(CommandSender sender, Player target, String reason) {
        sender.sendMessage("Pretending to ban " + target.getName() + " for: " + reason);
    }
}
