package me.ar1hurgit.commandframework.framework.completion;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ar1hurgit.commandframework.framework.context.CompletionContext;
import me.ar1hurgit.commandframework.framework.core.CommandParameterDescriptor;
import me.ar1hurgit.commandframework.framework.util.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class CompletionRegistry {

    private final Map<Class<?>, CompletionProvider> providers = new ConcurrentHashMap<>();
    private final EnumCompletionProvider enumCompletionProvider = new EnumCompletionProvider();

    public CompletionRegistry() {
        registerDefaults();
    }

    public <T> void register(Class<T> type, CompletionProvider provider) {
        providers.put(type, provider);
    }

    public List<String> complete(CompletionContext context, CommandParameterDescriptor parameter, String input) {
        if (!parameter.suggestions().isEmpty()) {
            return StringUtils.filterByPrefix(parameter.suggestions(), input);
        }

        CompletionProvider provider = providers.get(parameter.type());
        if (provider != null) {
            return provider.complete(context, parameter, input);
        }

        if (parameter.type().isEnum()) {
            return enumCompletionProvider.complete(context, parameter, input);
        }

        return List.of();
    }

    private void registerDefaults() {
        PlayerCompletionProvider playerCompletionProvider = new PlayerCompletionProvider();
        register(Player.class, playerCompletionProvider);
        register(OfflinePlayer.class, playerCompletionProvider);
        register(boolean.class, new BooleanCompletionProvider());
        register(Boolean.class, new BooleanCompletionProvider());
    }
}
