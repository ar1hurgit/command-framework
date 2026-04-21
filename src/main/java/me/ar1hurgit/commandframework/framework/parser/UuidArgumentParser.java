package me.ar1hurgit.commandframework.framework.parser;

import java.util.UUID;

import me.ar1hurgit.commandframework.framework.exception.ArgumentParseException;

public final class UuidArgumentParser implements ArgumentParser<UUID> {

    @Override
    public UUID parse(ParameterParseContext context) {
        try {
            return UUID.fromString(context.input());
        } catch (IllegalArgumentException exception) {
            throw new ArgumentParseException(context.parameter(), context.input(), "Invalid UUID.");
        }
    }
}
