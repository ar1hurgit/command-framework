package me.ar1hurgit.commandframework.framework.executor;

import java.util.Map;

import me.ar1hurgit.commandframework.framework.context.CommandContext;
import me.ar1hurgit.commandframework.framework.core.MessageKey;
import me.ar1hurgit.commandframework.framework.exception.ArgumentParseException;
import me.ar1hurgit.commandframework.framework.exception.CommandFrameworkException;
import me.ar1hurgit.commandframework.framework.exception.CooldownActiveException;
import me.ar1hurgit.commandframework.framework.exception.InvalidArgumentCountException;
import me.ar1hurgit.commandframework.framework.exception.InvalidSenderException;
import me.ar1hurgit.commandframework.framework.exception.PermissionDeniedException;
import me.ar1hurgit.commandframework.framework.exception.PlayerNotFoundException;
import me.ar1hurgit.commandframework.framework.exception.ValueOutOfRangeException;

public final class CommandErrorHandler {

    public void handle(CommandContext context, Throwable throwable) {
        if (throwable instanceof PermissionDeniedException) {
            context.reply(MessageKey.NO_PERMISSION, Map.of("command", context.definition().commandKey()));
            return;
        }
        if (throwable instanceof InvalidArgumentCountException) {
            context.reply(MessageKey.INVALID_ARGUMENT_COUNT, Map.of("usage", context.definition().resolveUsage()));
            return;
        }
        if (throwable instanceof ArgumentParseException exception) {
            context.reply(
                MessageKey.INVALID_ARGUMENT_TYPE,
                Map.of(
                    "parameter", exception.parameter().name(),
                    "type", exception.parameter().type().getSimpleName()
                )
            );
            return;
        }
        if (throwable instanceof InvalidSenderException exception) {
            context.reply(MessageKey.INVALID_SENDER, Map.of("expected", exception.expectedType().getSimpleName()));
            return;
        }
        if (throwable instanceof PlayerNotFoundException) {
            String input = context.invocationArguments().isEmpty() ? "unknown" : context.invocationArguments().get(0);
            context.reply(MessageKey.PLAYER_NOT_FOUND, Map.of("input", input));
            return;
        }
        if (throwable instanceof CooldownActiveException exception) {
            context.reply(
                MessageKey.COOLDOWN_ACTIVE,
                Map.of(
                    "seconds", Long.toString(exception.remainingSeconds()),
                    "command", context.definition().commandKey()
                )
            );
            return;
        }
        if (throwable instanceof ValueOutOfRangeException exception) {
            context.reply(
                MessageKey.VALUE_OUT_OF_RANGE,
                Map.of(
                    "parameter", exception.parameter().name(),
                    "min", Double.toString(exception.parameter().minimum()),
                    "max", Double.toString(exception.parameter().maximum())
                )
            );
            return;
        }

        context.reply(MessageKey.COMMAND_FAILED, Map.of());
        if (!(throwable instanceof CommandFrameworkException)) {
            context.plugin().getLogger().severe("Unhandled command exception for /" + context.definition().commandKey() + ": " + throwable.getMessage());
            throwable.printStackTrace();
        }
    }
}
