package me.ar1hurgit.commandframework.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class StringUtils {

    private StringUtils() {
    }

    public static List<String> splitCommandPath(String path) {
        return List.of(path.trim().split("\\s+")).stream()
            .filter(token -> !token.isBlank())
            .map(token -> token.toLowerCase(Locale.ROOT))
            .toList();
    }

    public static String applyPlaceholders(String template, Map<String, String> placeholders) {
        String resolved = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resolved;
    }

    public static List<String> filterByPrefix(Collection<String> values, String prefix) {
        String loweredPrefix = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        Set<String> filtered = new LinkedHashSet<>();
        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(loweredPrefix)) {
                filtered.add(value);
            }
        }
        return List.copyOf(filtered);
    }

    public static String joinTail(List<String> values, int fromIndex) {
        if (fromIndex >= values.size()) {
            return "";
        }
        return String.join(" ", values.subList(fromIndex, values.size()));
    }

    public static List<String> mutableLowercaseCopy(Collection<String> values) {
        List<String> lowered = new ArrayList<>(values.size());
        for (String value : values) {
            lowered.add(value.toLowerCase(Locale.ROOT));
        }
        return lowered;
    }
}
