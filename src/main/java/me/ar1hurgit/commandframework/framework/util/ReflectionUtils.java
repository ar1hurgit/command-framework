package me.ar1hurgit.commandframework.framework.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.ar1hurgit.commandframework.framework.annotation.Execute;
import me.ar1hurgit.commandframework.framework.exception.CommandDefinitionException;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }
    public static Method findExecuteMethod(Class<?> type) {
        List<Method> candidates = new ArrayList<>();
        for (Method method : type.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Execute.class)) {
                candidates.add(method);
            }
        }
        if (candidates.isEmpty()) {
            throw new CommandDefinitionException("No @Execute method was found in " + type.getSimpleName() + ".");
        }
        if (candidates.size() > 1) {
            throw new CommandDefinitionException("Only one @Execute method is allowed in " + type.getSimpleName() + ".");
        }

        Method method = candidates.get(0);
        method.setAccessible(true);
        return method;
    }

    public static MethodHandle bindHandle(Object instance, Method method) {
        try {
            return MethodHandles.lookup().unreflect(method).bindTo(instance);
        } catch (IllegalAccessException exception) {
            throw new CommandDefinitionException("Could not access @Execute method " + method.getName() + ".", exception);
        }
    }

    public static Object instantiate(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new CommandDefinitionException("Could not instantiate command class " + type.getSimpleName() + ".", exception);
        }
    }
}
