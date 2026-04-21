package me.ar1hurgit.commandframework.framework.completion;

import java.util.List;

import me.ar1hurgit.commandframework.framework.context.CompletionContext;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;
import me.ar1hurgit.commandframework.framework.util.StringUtils;
import org.bukkit.Bukkit;

public final class PlayerCompletionProvider implements CompletionProvider {

    @Override
    public List<String> complete(CompletionContext context, CommandParameterDescriptor parameter, String input) {
        return StringUtils.filterByPrefix(Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toList(), input);
    }
}
