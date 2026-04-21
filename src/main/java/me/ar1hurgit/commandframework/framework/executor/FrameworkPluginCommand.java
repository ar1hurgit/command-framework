package me.ar1hurgit.commandframework.framework.executor;

import java.util.List;

import me.ar1hurgit.commandframework.framework.registry.CommandRoot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public final class FrameworkPluginCommand extends Command {

    private final CommandRoot root;
    private final FrameworkCommandExecutor executor;

    public FrameworkPluginCommand(CommandRoot root, FrameworkCommandExecutor executor) {
        super(root.name());
        this.root = root;
        this.executor = executor;
        setDescription("Dynamic command root for " + root.name());
        setUsage("/" + root.name() + " help");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return executor.execute(sender, commandLabel, List.of(args), root);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return executor.complete(sender, alias, List.of(args), root);
    }
}
