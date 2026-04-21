package me.ar1hurgit.commandframework.demo.command;

import me.ar1hurgit.commandframework.framework.annotation.Alias;
import me.ar1hurgit.commandframework.framework.annotation.Command;
import me.ar1hurgit.commandframework.framework.annotation.Description;
import me.ar1hurgit.commandframework.framework.annotation.Execute;
import me.ar1hurgit.commandframework.framework.annotation.Permission;
import me.ar1hurgit.commandframework.framework.annotation.Usage;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@Command("ban history")
@Alias({"ban lookup"})
@Permission("staff.ban.history")
@Description("Inspect the moderation history of a player.")
@Usage("/ban history <player>")
public final class BanHistoryCommand {

    @Execute
    public void execute(CommandSender sender, OfflinePlayer target) {
        sender.sendMessage("History lookup for " + target.getName() + " is not wired to storage yet.");
    }
}
