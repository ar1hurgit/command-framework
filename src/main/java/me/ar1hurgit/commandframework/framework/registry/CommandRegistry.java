package me.ar1hurgit.commandframework.framework.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import me.ar1hurgit.commandframework.framework.core.CommandDefinition;

public final class CommandRegistry {

    private final CommandDefinitionFactory definitionFactory;
    private final BukkitCommandRegistrar registrar;
    private final Map<String, CommandRoot> roots = new LinkedHashMap<>();

    public CommandRegistry(CommandDefinitionFactory definitionFactory, BukkitCommandRegistrar registrar) {
        this.definitionFactory = definitionFactory;
        this.registrar = registrar;
    }

    public void register(Object commandInstance) {
        CommandDefinition definition = definitionFactory.create(commandInstance);
        CommandRoot root = roots.get(definition.rootName());
        if (root == null) {
            root = new CommandRoot(definition.rootName());
            roots.put(definition.rootName(), root);
            root.addRoute(definition);
            registrar.register(root);
            return;
        }
        root.addRoute(definition);
    }

    public CommandRoot findRoot(String name) {
        return roots.get(name.toLowerCase());
    }

    public Collection<CommandRoot> roots() {
        return roots.values();
    }
}
