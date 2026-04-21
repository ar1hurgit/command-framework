package me.ar1hurgit.commandframework.framework.validation;

import me.ar1hurgit.commandframework.framework.context.CommandContext;

public interface CommandMiddleware {

    default void beforeExecute(CommandContext context) {
    }

    default void afterExecute(CommandContext context) {
    }

    default void onError(CommandContext context, Throwable throwable) {
    }
}
