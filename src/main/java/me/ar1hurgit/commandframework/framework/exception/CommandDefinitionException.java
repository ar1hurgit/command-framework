package me.ar1hurgit.commandframework.framework.exception;

public final class CommandDefinitionException extends CommandFrameworkException {

    public CommandDefinitionException(String message) {
        super(message);
    }

    public CommandDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
