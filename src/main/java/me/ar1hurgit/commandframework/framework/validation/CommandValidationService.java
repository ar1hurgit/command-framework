package me.ar1hurgit.commandframework.framework.validation;

import java.time.Duration;
import java.util.List;

import me.ar1hurgit.commandframework.framework.context.CommandContext;
import me.ar1hurgit.commandframework.framework.core.CommandDefinition;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;
import me.ar1hurgit.commandframework.framework.exception.CooldownActiveException;
import me.ar1hurgit.commandframework.framework.exception.InvalidArgumentCountException;
import me.ar1hurgit.commandframework.framework.exception.InvalidSenderException;
import me.ar1hurgit.commandframework.framework.exception.PermissionDeniedException;
import me.ar1hurgit.commandframework.framework.exception.ValueOutOfRangeException;
import org.bukkit.command.CommandSender;

public final class CommandValidationService {

    private final CooldownService cooldownService;

    public CommandValidationService(CooldownService cooldownService) {
        this.cooldownService = cooldownService;
    }

    public void validateBeforeParsing(CommandContext context) {
        validatePermission(context.sender(), context.definition());
        validateSender(context.sender(), context.definition());
        validateCooldown(context);
        validateArgumentCount(context.definition(), context.invocationArguments());
    }

    public void validateRange(CommandParameterDescriptor parameter, Object value) {
        if (!parameter.hasRange() || !(value instanceof Number number)) {
            return;
        }

        double numericValue = number.doubleValue();
        if (numericValue < parameter.minimum() || numericValue > parameter.maximum()) {
            throw new ValueOutOfRangeException(parameter, "Value out of range.");
        }
    }

    private void validatePermission(CommandSender sender, CommandDefinition definition) {
        String permission = definition.permission();
        if (permission != null && !permission.isBlank() && !sender.hasPermission(permission)) {
            throw new PermissionDeniedException("Missing permission.");
        }
    }

    private void validateSender(CommandSender sender, CommandDefinition definition) {
        CommandParameterDescriptor senderParameter = definition.senderParameter();
        if (senderParameter == null) {
            return;
        }
        if (!senderParameter.type().isInstance(sender)) {
            throw new InvalidSenderException("Invalid sender.", senderParameter.type());
        }
    }

    private void validateCooldown(CommandContext context) {
        Duration remaining = cooldownService.remaining(context.sender(), context.definition());
        if (!remaining.isZero() && !remaining.isNegative()) {
            throw new CooldownActiveException("Cooldown active.", Math.max(1L, remaining.toSeconds()));
        }
    }

    private void validateArgumentCount(CommandDefinition definition, List<String> arguments) {
        int required = definition.requiredArguments();
        int maximum = definition.maximumArguments();
        int actual = arguments.size();
        if (actual < required || actual > maximum) {
            throw new InvalidArgumentCountException("Invalid argument count.");
        }
    }
}

