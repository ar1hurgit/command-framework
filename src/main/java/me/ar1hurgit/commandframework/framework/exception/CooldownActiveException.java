package me.ar1hurgit.commandframework.framework.exception;

public final class CooldownActiveException extends CommandFrameworkException {

    private final long remainingSeconds;

    public CooldownActiveException(String message, long remainingSeconds) {
        super(message);
        this.remainingSeconds = remainingSeconds;
    }

    public long remainingSeconds() {
        return remainingSeconds;
    }
}
