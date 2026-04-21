package me.ar1hurgit.commandframework.framework.exception;

public final class InvalidSenderException extends CommandFrameworkException {

    private final Class<?> expectedType;

    public InvalidSenderException(String message, Class<?> expectedType) {
        super(message);
        this.expectedType = expectedType;
    }

    public Class<?> expectedType() {
        return expectedType;
    }
}
