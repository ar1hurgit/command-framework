package me.ar1hurgit.commandframework.framework.completion;

import java.util.Arrays;
import java.util.List;

import me.ar1hurgit.commandframework.framework.context.CompletionContext;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;
import me.ar1hurgit.commandframework.framework.util.StringUtils;

public final class EnumCompletionProvider implements CompletionProvider {

    @Override
    public List<String> complete(CompletionContext context, CommandParameterDescriptor parameter, String input) {
        return StringUtils.filterByPrefix(
            Arrays.stream(parameter.type().getEnumConstants()).map(constant -> ((Enum<?>) constant).name().toLowerCase()).toList(),
            input
        );
    }
}
