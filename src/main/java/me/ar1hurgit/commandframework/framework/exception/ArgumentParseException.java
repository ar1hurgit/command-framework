package me.ar1hurgit.commandframework.framework.exception;

import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;

public final class ArgumentParseException extends CommandFrameworkException {

    private final CommandParameterDescriptor parameter;
    private final String input;

    public ArgumentParseException(CommandParameterDescriptor parameter, String input, String message) {
        super(message);
        this.parameter = parameter;
        this.input = input;
    }

    public CommandParameterDescriptor parameter() {
        return parameter;
    }

    public String input() {
        return input;
    }
}
