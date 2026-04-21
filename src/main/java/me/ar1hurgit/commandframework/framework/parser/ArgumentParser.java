package me.ar1hurgit.commandframework.framework.parser;

@FunctionalInterface
public interface ArgumentParser<T> {

    T parse(ParameterParseContext context);
}
