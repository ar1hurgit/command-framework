package me.ar1hurgit.commandframework.framework.core;

import java.util.Map;

public interface MessageResolver {

    String resolve(MessageKey key, Map<String, String> placeholders);
}
