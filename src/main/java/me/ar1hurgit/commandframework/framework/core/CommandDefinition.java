package me.ar1hurgit.commandframework.framework.core;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class CommandDefinition {

    private final Object instance;
    private final Method method;
    private final MethodHandle methodHandle;
    private final List<String> fullPathTokens;
    private final List<List<String>> routePaths;
    private final Set<String> rootAliases;
    private final List<CommandParameterDescriptor> parameters;
    private final String description;
    private final String usage;
    private final String permission;
    private final long cooldownSeconds;
    private final boolean async;

    public CommandDefinition(
        Object instance,
        Method method,
        MethodHandle methodHandle,
        List<String> fullPathTokens,
        List<List<String>> routePaths,
        Set<String> rootAliases,
        List<CommandParameterDescriptor> parameters,
        String description,
        String usage,
        String permission,
        long cooldownSeconds,
        boolean async
    ) {
        this.instance = Objects.requireNonNull(instance, "instance");
        this.method = Objects.requireNonNull(method, "method");
        this.methodHandle = Objects.requireNonNull(methodHandle, "methodHandle");
        this.fullPathTokens = List.copyOf(fullPathTokens);
        this.routePaths = routePaths.stream().map(List::copyOf).toList();
        this.rootAliases = Set.copyOf(rootAliases);
        this.parameters = List.copyOf(parameters);
        this.description = description == null || description.isBlank() ? "No description provided." : description;
        this.usage = usage;
        this.permission = permission;
        this.cooldownSeconds = Math.max(0L, cooldownSeconds);
        this.async = async;
    }

    public Object instance() {
        return instance;
    }

    public Method method() {
        return method;
    }

    public List<String> fullPathTokens() {
        return fullPathTokens;
    }

    public String rootName() {
        return fullPathTokens.get(0);
    }

    public List<String> canonicalLiteralPath() {
        return fullPathTokens.subList(1, fullPathTokens.size());
    }

    public List<List<String>> routePaths() {
        return routePaths;
    }

    public Set<String> rootAliases() {
        return rootAliases;
    }

    public List<CommandParameterDescriptor> parameters() {
        return parameters;
    }

    public List<CommandParameterDescriptor> argumentParameters() {
        return parameters.stream()
            .filter(parameter -> parameter.kind() == CommandParameterDescriptor.ParameterKind.ARGUMENT)
            .toList();
    }

    public CommandParameterDescriptor senderParameter() {
        return parameters.stream()
            .filter(parameter -> parameter.kind() == CommandParameterDescriptor.ParameterKind.SENDER)
            .findFirst()
            .orElse(null);
    }

    public String description() {
        return description;
    }

    public String usage() {
        return usage;
    }

    public String permission() {
        return permission;
    }

    public long cooldownSeconds() {
        return cooldownSeconds;
    }

    public boolean async() {
        return async;
    }

    public String commandKey() {
        return String.join(" ", fullPathTokens);
    }

    public boolean acceptsUnlimitedArguments() {
        List<CommandParameterDescriptor> argumentParameters = argumentParameters();
        return !argumentParameters.isEmpty() && argumentParameters.get(argumentParameters.size() - 1).greedy();
    }

    public int requiredArguments() {
        int required = 0;
        for (CommandParameterDescriptor parameter : argumentParameters()) {
            if (!parameter.optional()) {
                required++;
            }
        }
        return required;
    }

    public int maximumArguments() {
        return acceptsUnlimitedArguments() ? Integer.MAX_VALUE : argumentParameters().size();
    }

    public String resolveUsage() {
        if (usage != null && !usage.isBlank()) {
            return usage;
        }

        List<String> tokens = new ArrayList<>(fullPathTokens);
        for (CommandParameterDescriptor parameter : argumentParameters()) {
            tokens.add(parameter.optional() ? "[" + parameter.name() + "]" : "<" + parameter.name() + ">");
        }
        return "/" + String.join(" ", tokens);
    }

    public Set<String> allLiteralPaths() {
        Set<String> paths = new LinkedHashSet<>();
        for (List<String> routePath : routePaths) {
            paths.add(String.join(" ", routePath));
        }
        return paths;
    }

    public Object invoke(List<Object> invocationArguments) throws Throwable {
        return methodHandle.invokeWithArguments(invocationArguments);
    }
}

