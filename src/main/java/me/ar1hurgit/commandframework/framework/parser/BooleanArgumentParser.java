package me.ar1hurgit.commandframework.framework.parser;

import java.util.Locale;
import java.util.Set;

import me.ar1hurgit.commandframework.framework.exception.ArgumentParseException;

public final class BooleanArgumentParser implements ArgumentParser<Boolean> {

    private static final Set<String> TRUE_VALUES = Set.of("true", "yes", "on", "1");
    private static final Set<String> FALSE_VALUES = Set.of("false", "no", "off", "0");

    @Override
    public Boolean parse(ParameterParseContext context) {
        String normalized = context.input().toLowerCase(Locale.ROOT);
        if (TRUE_VALUES.contains(normalized)) {
            return true;
        }
        if (FALSE_VALUES.contains(normalized)) {
            return false;
        }
        throw new ArgumentParseException(context.parameter(), context.input(), "Invalid boolean.");
    }
}
