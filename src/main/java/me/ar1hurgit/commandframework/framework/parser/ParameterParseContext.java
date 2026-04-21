package me.ar1hurgit.commandframework.framework.parser;

import java.util.List;

import me.ar1hurgit.commandframework.framework.context.CommandContext;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;

public record ParameterParseContext(
    CommandContext commandContext,
    CommandParameterDescriptor parameter,
    String input,
    List<Object> parsedArguments,
    List<String> remainingArguments
) {
}
