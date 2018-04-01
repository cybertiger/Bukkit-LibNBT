/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public class CBShim {

    private static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
    private static final Map<Class, Class> PRIMITIVE_TYPES = new HashMap<Class,Class>();
    static {
        PRIMITIVE_TYPES.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TYPES.put(Byte.TYPE, Byte.class);
        PRIMITIVE_TYPES.put(Short.TYPE, Short.class);
        PRIMITIVE_TYPES.put(Integer.TYPE, Integer.class);
        PRIMITIVE_TYPES.put(Long.TYPE, Long.class);
        PRIMITIVE_TYPES.put(Float.TYPE, Float.class);
        PRIMITIVE_TYPES.put(Double.TYPE, Double.class);
        PRIMITIVE_TYPES.put(Character.TYPE, Character.class);
        PRIMITIVE_TYPES.put(Void.TYPE, Void.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createShim(Class<T> type, Plugin plugin, Object... args) {
        String version = getNmsVersion(plugin);
        try {
            version = Optional.ofNullable(loadVersioning(type, version))
                    .map(VersionedNMS::getTargetVersion)
                    .orElse(version);
            Class<? extends T> resultType = loadShim(type, version);
            T result = newInstance(resultType, args);
            plugin.getLogger().info("Loaded " + type.getName() + " as " + resultType.getName());
            return result;
        } catch (ReflectiveOperationException | IllegalArgumentException ex) {
            unsupportedVersion(plugin.getServer(), ex);
        }
        // Unreachable
        return null;
    }

    /**
     * Get the versioning on NMS.
     * @param plugin A plugin loaded by the server.
     * @return A string such as v1_12_R1 used in the package name of NMS.
     */
    private static String getNmsVersion(Plugin plugin) {
        Class<?> serverClass = plugin.getServer().getClass();
        while (!serverClass.getPackage().getName().startsWith(CRAFTBUKKIT_PACKAGE) ) {
            serverClass = serverClass.getSuperclass();
            if (serverClass == null) {
                unsupportedVersion(plugin.getServer());
            }
        }
        String pkg  = serverClass.getPackage().getName();
        int i = pkg.lastIndexOf(".");
        if (i == -1) {
            unsupportedVersion(plugin.getServer());
        }
        return pkg.substring(i+1);
    }

    @SuppressWarnings("unchecked")
    private static <T> T newInstance(Class<? extends T> type, Object[] args) throws ReflectiveOperationException {
        LOOP:
        for (Constructor constructor : type.getConstructors()) {
            Class[] parameterTypes = constructor.getParameterTypes();
            if (args.length != parameterTypes.length) {
                continue LOOP;
            }
            for (int i = 0; i < parameterTypes.length; i++) {
                Class parameterType = parameterTypes[i];
                if (PRIMITIVE_TYPES.containsKey(parameterType))
                    parameterType = PRIMITIVE_TYPES.get(parameterType);
                if (!parameterType.isInstance(args[i])) {
                    continue LOOP;
                }
            }
            return (T) constructor.newInstance(args);
        }
        throw new IllegalArgumentException("Constructor with arguments :" + Arrays.toString(args) + " not found");
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> loadShim(Class<T> type, String version) throws ReflectiveOperationException {
        String className = type.getPackage().getName() + '.' + version + '.' + type.getSimpleName();
        return (Class<? extends T>) Class.forName(className);
    }

    @SuppressWarnings("unchecked")
    private static VersionedNMS loadVersioning(Class<?> type, String version) {
        try {
            String className = type.getPackage().getName() + '.' + version + '.' + type.getSimpleName() + "VersionedNMS";
            Class<?> versioningType = Class.forName(className);
            if (VersionedNMS.class.isAssignableFrom(versioningType)) {
                return (VersionedNMS) versioningType.newInstance();
            }
        } catch (ReflectiveOperationException ex) {
            // ignored.
        }
        return null;
    }

    private static void unsupportedVersion(Server server) {
        unsupportedVersion(server, null);
    }
    private static void unsupportedVersion(Server server, Exception ex) {
        throw new UnsupportedOperationException("Unsupported CraftBukkit version: " + server.getBukkitVersion(), ex);
    }
    
}
