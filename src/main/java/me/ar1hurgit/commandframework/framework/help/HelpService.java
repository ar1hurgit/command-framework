package me.ar1hurgit.commandframework.framework.help;

import java.util.List;
import java.util.Map;

import me.ar1hurgit.commandframework.framework.context.CommandContext;
import me.ar1hurgit.commandframework.framework.core.CommandDefinition;
import me.ar1hurgit.commandframework.framework.core.MessageKey;
import me.ar1hurgit.commandframework.framework.registry.CommandRoot;
import org.bukkit.command.CommandSender;

public final class HelpService {

    public void sendRootHelp(CommandSender sender, CommandRoot root, CommandContext context) {
        sender.sendMessage(context.framework().messageResolver().resolve(MessageKey.HELP_HEADER, Map.of("command", root.name())));

        List<CommandDefinition> visibleCommands = root.routes().stream()
            .filter(definition -> definition.permission() == null || sender.hasPermission(definition.permission()))
            .sorted((left, right) -> left.commandKey().compareToIgnoreCase(right.commandKey()))
            .toList();

        if (visibleCommands.isEmpty()) {
            sender.sendMessage(context.framework().messageResolver().resolve(MessageKey.HELP_EMPTY, Map.of()));
            return;
        }

        for (CommandDefinition definition : visibleCommands) {
            sender.sendMessage(
                context.framework().messageResolver().resolve(
                    MessageKey.HELP_ENTRY,
                    Map.of(
                        "usage", definition.resolveUsage(),
                        "description", definition.description()
                    )
                )
            );
        }
    }
}
