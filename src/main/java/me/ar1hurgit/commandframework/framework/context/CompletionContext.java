package me.ar1hurgit.commandframework.framework.context;

import java.util.List;

import me.ar1hurgit.commandframework.framework.core.CommandDefinition;
import me.ar1hurgit.commandframework.framework.core.CommandFramework;
import org.bukkit.command.CommandSender;

public record CompletionContext(
    CommandFramework framework,
    CommandSender sender,
    CommandDefinition definition,
    List<String> rawArguments,
    List<String> committedArguments,
    String currentInput
) {
}
