package me.ar1hurgit.commandframework.framework.core;

import java.util.List;
import java.util.Objects;

public record CommandParameterDescriptor(
    int methodIndex,
    ParameterKind kind,
    String name,
    Class<?> type,
    boolean optional,
    Double minimum,
    Double maximum,
    List<String> suggestions,
    boolean greedy
) {

    public CommandParameterDescriptor {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        suggestions = List.copyOf(suggestions);
    }

    public boolean hasRange() {
        return minimum != null && maximum != null;
    }

    public Object defaultValue() {
        if (!optional) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == double.class) {
            return 0D;
        }
        if (type == long.class) {
            return 0L;
        }
        return null;
    }

    public enum ParameterKind {
        SENDER,
        CONTEXT,
        ARGUMENT
    }
}
