package me.ar1hurgit.commandframework.framework.exception;

import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;

public final class ValueOutOfRangeException extends CommandFrameworkException {

    private final CommandParameterDescriptor parameter;

    public ValueOutOfRangeException(CommandParameterDescriptor parameter, String message) {
        super(message);
        this.parameter = parameter;
    }

    public CommandParameterDescriptor parameter() {
        return parameter;
    }
}
