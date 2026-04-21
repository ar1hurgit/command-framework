package me.ar1hurgit.commandframework.framework.parser;

import me.ar1hurgit.commandframework.framework.exception.ArgumentParseException;

public final class DoubleArgumentParser implements ArgumentParser<Double> {

    @Override
    public Double parse(ParameterParseContext context) {
        try {
            return Double.parseDouble(context.input());
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(context.parameter(), context.input(), "Invalid double.");
        }
    }
}
