package me.ar1hurgit.commandframework.framework.registry;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import me.ar1hurgit.commandframework.framework.core.CommandDefinition;
import me.ar1hurgit.commandframework.framework.util.StringUtils;

public final class CommandRoot {

    private final String name;
    private final List<CommandDefinition> routes = new ArrayList<>();
    private final Set<String> aliases = new LinkedHashSet<>();

    public CommandRoot(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    public String name() {
        return name;
    }

    public void addRoute(CommandDefinition definition) {
        routes.add(definition);
        aliases.addAll(definition.rootAliases());
        routes.sort((left, right) -> Integer.compare(longestPath(right), longestPath(left)));
    }

    public List<CommandDefinition> routes() {
        return List.copyOf(routes);
    }

    public Set<String> aliases() {
        return Set.copyOf(aliases);
    }

    public boolean hasSubcommands() {
        return routes.stream().anyMatch(definition -> !definition.canonicalLiteralPath().isEmpty());
    }

    public Optional<RouteMatch> match(List<String> arguments) {
        for (CommandDefinition definition : routes) {
            for (List<String> path : definition.routePaths()) {
                if (arguments.size() < path.size()) {
                    continue;
                }
                if (matchesPath(arguments, path)) {
                    return Optional.of(new RouteMatch(definition, path, List.copyOf(arguments.subList(path.size(), arguments.size()))));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<RouteMatch> matchForCompletion(List<String> committedArguments) {
        for (CommandDefinition definition : routes) {
            for (List<String> path : definition.routePaths()) {
                if (committedArguments.size() < path.size()) {
                    continue;
                }
                if (matchesPath(committedArguments, path)) {
                    return Optional.of(new RouteMatch(definition, path, List.copyOf(committedArguments.subList(path.size(), committedArguments.size()))));
                }
            }
        }
        return Optional.empty();
    }

    public List<String> suggestLiteral(List<String> committedArguments, String currentInput) {
        Set<String> suggestions = new LinkedHashSet<>();
        for (CommandDefinition definition : routes) {
            for (List<String> path : definition.routePaths()) {
                int position = committedArguments.size();
                if (position >= path.size()) {
                    continue;
                }
                if (prefixMatches(committedArguments, path)) {
                    suggestions.add(path.get(position));
                }
            }
        }
        boolean executableRootExists = routes.stream().anyMatch(definition -> definition.routePaths().stream().anyMatch(List::isEmpty));
        if (committedArguments.isEmpty() && !executableRootExists) {
            suggestions.add("help");
        }
        return StringUtils.filterByPrefix(suggestions, currentInput);
    }

    private boolean matchesPath(List<String> arguments, List<String> path) {
        for (int index = 0; index < path.size(); index++) {
            if (!arguments.get(index).equalsIgnoreCase(path.get(index))) {
                return false;
            }
        }
        return true;
    }

    private boolean prefixMatches(List<String> committedArguments, List<String> path) {
        for (int index = 0; index < committedArguments.size(); index++) {
            if (!committedArguments.get(index).equalsIgnoreCase(path.get(index))) {
                return false;
            }
        }
        return true;
    }

    private int longestPath(CommandDefinition definition) {
        return definition.routePaths().stream().mapToInt(List::size).max().orElse(0);
    }
}
