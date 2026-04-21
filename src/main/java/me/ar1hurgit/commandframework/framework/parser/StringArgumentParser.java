package me.ar1hurgit.commandframework.framework.parser;

public final class StringArgumentParser implements ArgumentParser<String> {

    @Override
    public String parse(ParameterParseContext context) {
        return context.input();
    }
}
