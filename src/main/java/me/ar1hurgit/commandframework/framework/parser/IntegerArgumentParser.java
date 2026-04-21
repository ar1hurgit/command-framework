package me.ar1hurgit.commandframework.framework.parser;

import me.ar1hurgit.commandframework.framework.exception.ArgumentParseException;

public final class IntegerArgumentParser implements ArgumentParser<Integer> {

    @Override
    public Integer parse(ParameterParseContext context) {
        try {
            return Integer.parseInt(context.input());
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(context.parameter(), context.input(), "Invalid integer.");
        }
    }
}
