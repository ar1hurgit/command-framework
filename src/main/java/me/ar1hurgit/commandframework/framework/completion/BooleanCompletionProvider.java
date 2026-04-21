package me.ar1hurgit.commandframework.framework.completion;

import java.util.List;

import me.ar1hurgit.commandframework.framework.context.CompletionContext;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;
import me.ar1hurgit.commandframework.framework.util.StringUtils;

public final class BooleanCompletionProvider implements CompletionProvider {

    @Override
    public List<String> complete(CompletionContext context, CommandParameterDescriptor parameter, String input) {
        return StringUtils.filterByPrefix(List.of("true", "false"), input);
    }
}
