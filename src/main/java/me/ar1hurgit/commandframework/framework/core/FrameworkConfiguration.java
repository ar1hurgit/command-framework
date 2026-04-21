package me.ar1hurgit.commandframework.framework.core;

public record FrameworkConfiguration(
    MessageResolver messageResolver,
    boolean metricsEnabled
) {

    public static FrameworkConfiguration defaultConfiguration() {
        return new FrameworkConfiguration(new DefaultMessageResolver(), true);
    }
}
