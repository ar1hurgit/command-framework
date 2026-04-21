package me.ar1hurgit.commandframework.demo.command;

import me.ar1hurgit.commandframework.framework.annotation.Async;
import me.ar1hurgit.commandframework.framework.annotation.Command;
import me.ar1hurgit.commandframework.framework.annotation.Description;
import me.ar1hurgit.commandframework.framework.annotation.Execute;
import me.ar1hurgit.commandframework.framework.annotation.Optional;
import me.ar1hurgit.commandframework.framework.annotation.Range;
import me.ar1hurgit.commandframework.framework.context.CommandContext;
import org.bukkit.command.CommandSender;

@Command("ping")
@Description("Simple async demo command with range validation.")
@Async
public final class PingCommand {
    @Execute
    public void execute(CommandContext context, CommandSender sender, @Optional @Range(min = 1, max = 5) Integer amount) {
        int value = amount == null ? 1 : amount;
        context.plugin().getServer().getScheduler().runTask(context.plugin(), () -> sender.sendMessage("pong x" + value));
    }
}
