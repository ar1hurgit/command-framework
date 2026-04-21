package me.ar1hurgit.commandframework.framework.parser;

import java.util.Arrays;

import me.ar1hurgit.commandframework.framework.exception.ArgumentParseException;

public final class EnumArgumentParser implements ArgumentParser<Enum<?>> {

    @Override
    public Enum<?> parse(ParameterParseContext context) {
        Class<?> type = context.parameter().type();
        Object[] constants = type.getEnumConstants();
        return Arrays.stream(constants)
            .map(Enum.class::cast)
            .filter(value -> value.name().equalsIgnoreCase(context.input()))
            .findFirst()
            .orElseThrow(() -> new ArgumentParseException(context.parameter(), context.input(), "Invalid enum constant."));
    }
}
