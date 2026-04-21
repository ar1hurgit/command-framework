package me.ar1hurgit.commandframework.framework.core;

import java.util.EnumMap;
import java.util.Map;

import me.ar1hurgit.commandframework.framework.util.StringUtils;

public final class DefaultMessageResolver implements MessageResolver {

    private final Map<MessageKey, String> templates = new EnumMap<>(MessageKey.class);

    public DefaultMessageResolver() {
        templates.put(MessageKey.NO_PERMISSION, "&cYou do not have permission to run &f/{command}&c.");
        templates.put(MessageKey.INVALID_ARGUMENT_COUNT, "&cInvalid usage. Try &f{usage}&c.");
        templates.put(MessageKey.INVALID_ARGUMENT_TYPE, "&cInvalid value for &f{parameter}&c. Expected &f{type}&c.");
        templates.put(MessageKey.INVALID_SENDER, "&cThis command requires a &f{expected}&c sender.");
        templates.put(MessageKey.PLAYER_NOT_FOUND, "&cPlayer &f{input}&c could not be found.");
        templates.put(MessageKey.COOLDOWN_ACTIVE, "&cYou must wait &f{seconds}s &cbefore using &f/{command}&c again.");
        templates.put(MessageKey.VALUE_OUT_OF_RANGE, "&cValue for &f{parameter}&c must be between &f{min}&c and &f{max}&c.");
        templates.put(MessageKey.UNKNOWN_SUBCOMMAND, "&cUnknown subcommand. Try &f/{command} help&c.");
        templates.put(MessageKey.COMMAND_FAILED, "&cAn internal error occurred while executing this command.");
        templates.put(MessageKey.HELP_HEADER, "&6Help for &e/{command}");
        templates.put(MessageKey.HELP_ENTRY, "&e{usage} &7- {description}");
        templates.put(MessageKey.HELP_EMPTY, "&7No command entries are available.");
    }

    @Override
    public String resolve(MessageKey key, Map<String, String> placeholders) {
        String template = templates.getOrDefault(key, key.name());
        return StringUtils.applyPlaceholders(template, placeholders);
    }
}
