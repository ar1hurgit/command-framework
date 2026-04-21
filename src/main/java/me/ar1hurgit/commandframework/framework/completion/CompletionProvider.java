package me.ar1hurgit.commandframework.framework.completion;

import java.util.List;

import me.ar1hurgit.commandframework.framework.context.CompletionContext;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;

@FunctionalInterface
public interface CompletionProvider {

    List<String> complete(CompletionContext context, CommandParameterDescriptor parameter, String input);
}
