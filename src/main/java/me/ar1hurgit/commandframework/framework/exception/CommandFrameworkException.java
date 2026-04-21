package me.ar1hurgit.commandframework.framework.exception;

public class CommandFrameworkException extends RuntimeException {

    public CommandFrameworkException(String message) {
        super(message);
    }

    public CommandFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
