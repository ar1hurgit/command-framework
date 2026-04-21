package me.ar1hurgit.commandframework.framework.parser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ar1hurgit.commandframework.framework.exception.CommandDefinitionException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class ParserRegistry {

    private final Map<Class<?>, ArgumentParser<?>> parsers = new ConcurrentHashMap<>();
    private final EnumArgumentParser enumArgumentParser = new EnumArgumentParser();

    public ParserRegistry() {
        registerDefaults();
    }

    public <T> void register(Class<T> type, ArgumentParser<? extends T> parser) {
        parsers.put(type, parser);
    }

    public ArgumentParser<?> resolve(Class<?> type) {
        ArgumentParser<?> parser = parsers.get(type);
        if (parser != null) {
            return parser;
        }
        if (type.isEnum()) {
            return enumArgumentParser;
        }
        throw new CommandDefinitionException("No parser registered for " + type.getSimpleName() + ".");
    }

    public List<Class<?>> registeredTypes() {
        return List.copyOf(parsers.keySet());
    }

    private void registerDefaults() {
        register(String.class, new StringArgumentParser());
        register(int.class, new IntegerArgumentParser());
        register(Integer.class, new IntegerArgumentParser());
        register(double.class, new DoubleArgumentParser());
        register(Double.class, new DoubleArgumentParser());
        register(boolean.class, new BooleanArgumentParser());
        register(Boolean.class, new BooleanArgumentParser());
        register(Player.class, new PlayerArgumentParser());
        register(OfflinePlayer.class, new OfflinePlayerArgumentParser());
        register(java.util.UUID.class, new UuidArgumentParser());
    }
}
