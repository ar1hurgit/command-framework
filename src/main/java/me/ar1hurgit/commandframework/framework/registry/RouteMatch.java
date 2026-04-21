package me.ar1hurgit.commandframework.framework.registry;

import java.util.List;

import me.ar1hurgit.commandframework.framework.core.CommandDefinition;

public record RouteMatch(
    CommandDefinition definition,
    List<String> matchedPath,
    List<String> remainingArguments
) {
}
