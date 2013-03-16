/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public class CBShim {

    private static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
    private static final Map<Class<?>,Object> cache = new HashMap<Class<?>, Object>();

    public static <T> T createShim(Class<T> type, Plugin plugin, Object... args) {
        T ret = (T) cache.get(type);
        if (ret != null) {
            return ret;
        }
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
        String childPackage = pkg.substring(i+1);
        String className = type.getPackage().getName() + '.' + childPackage + '.' + type.getSimpleName();
        try {
            Class<T> typeClass = (Class<T>) CBShim.class.getClassLoader().loadClass(className);
            Constructor[] constructors = typeClass.getConstructors();
            LOOP:
            for (Constructor constructor : constructors) {
                Class[] parameterTypes = constructor.getParameterTypes();
                if (args.length != parameterTypes.length)
                    continue LOOP;
                for (i = 0; i < parameterTypes.length; i++) {
                    Class parameterType = parameterTypes[i];
                    if (!parameterType.isInstance(args))
                        continue LOOP;
                }
                ret = (T) constructor.newInstance(args);
                break LOOP;
            }
            cache.put(type, ret);
            return ret;
        } catch (ClassNotFoundException ex) {
            unsupportedVersion(plugin.getServer(), ex);
        } catch (InstantiationException ex) {
            unsupportedVersion(plugin.getServer(), ex);
        } catch (IllegalAccessException ex) {
            unsupportedVersion(plugin.getServer(), ex);
        } catch (IllegalArgumentException ex) {
            unsupportedVersion(plugin.getServer(), ex);
        } catch (InvocationTargetException ex) {
            unsupportedVersion(plugin.getServer(), ex);
        }
        // unreached, stupid compiler.
        return null;
    }

    private static void unsupportedVersion(Server server) {
        unsupportedVersion(server, null);
    }
    private static void unsupportedVersion(Server server, Exception ex) {
        throw new UnsupportedOperationException("Unsupported CraftBukkit version: " + server.getBukkitVersion(), ex);
    }
    
}
