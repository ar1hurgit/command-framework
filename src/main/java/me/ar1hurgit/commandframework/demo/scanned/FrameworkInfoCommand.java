package me.ar1hurgit.commandframework.demo.scanned;

import me.ar1hurgit.commandframework.framework.annotation.Command;
import me.ar1hurgit.commandframework.framework.annotation.Description;
import me.ar1hurgit.commandframework.framework.annotation.Execute;
import org.bukkit.command.CommandSender;

@Command("framework info")
@Description("Shows that package scanning can register command classes automatically.")
public final class FrameworkInfoCommand {

    @Execute
    public void execute(CommandSender sender) {
        sender.sendMessage("Command Framework is alive and scanned this command automatically.");
    }
}
