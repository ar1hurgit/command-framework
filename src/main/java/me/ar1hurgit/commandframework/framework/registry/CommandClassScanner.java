package me.ar1hurgit.commandframework.framework.registry;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.ar1hurgit.commandframework.framework.annotation.Command;
import me.ar1hurgit.commandframework.framework.exception.CommandDefinitionException;
import me.ar1hurgit.commandframework.framework.util.ReflectionUtils;

public final class CommandClassScanner {

    private final ClassLoader classLoader;

    public CommandClassScanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Set<Object> scan(String basePackage) {
        String packagePath = basePackage.replace('.', '/');
        Set<Object> discovered = new LinkedHashSet<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    scanDirectory(basePackage, new File(resource.toURI()), discovered);
                    continue;
                }
                if ("jar".equals(resource.getProtocol())) {
                    scanJar(basePackage, resource, discovered);
                }
            }
        } catch (IOException | URISyntaxException exception) {
            throw new CommandDefinitionException("Failed to scan package " + basePackage + ".", exception);
        }
        return discovered;
    }

    private void scanDirectory(String basePackage, File directory, Set<Object> discovered) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(basePackage + "." + file.getName(), file, discovered);
                continue;
            }
            if (!file.getName().endsWith(".class") || file.getName().contains("$")) {
                continue;
            }
            String className = basePackage + "." + file.getName().replace(".class", "");
            inspectClass(className, discovered);
        }
    }

    private void scanJar(String basePackage, URL resource, Set<Object> discovered) throws IOException {
        JarURLConnection connection = (JarURLConnection) resource.openConnection();
        try (JarFile jarFile = connection.getJarFile()) {
            String packagePath = basePackage.replace('.', '/');
            for (JarEntry entry : java.util.Collections.list(jarFile.entries())) {
                String name = entry.getName();
                if (!name.startsWith(packagePath) || !name.endsWith(".class") || name.contains("$")) {
                    continue;
                }
                inspectClass(name.replace('/', '.').replace(".class", ""), discovered);
            }
        }
    }

    private void inspectClass(String className, Set<Object> discovered) {
        try {
            Class<?> type = Class.forName(className, false, classLoader);
            if (type.isInterface() || java.lang.reflect.Modifier.isAbstract(type.getModifiers())) {
                return;
            }
            if (type.isAnnotationPresent(Command.class)) {
                discovered.add(ReflectionUtils.instantiate(type));
            }
        } catch (ReflectiveOperationException exception) {
            throw new CommandDefinitionException("Failed to inspect " + className + ".", exception);
        }
    }
}
