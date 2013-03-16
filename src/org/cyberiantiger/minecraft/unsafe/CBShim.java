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

/**
 *
 * @author antony
 */
public class CBShim {

    private static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
    private static final Map<Class<?>,Object> cache = new HashMap<Class<?>, Object>();

    public static <T> T getShim(Class<T> type, Server server, Object... args) {
        T ret = (T) cache.get(type);
        if (ret != null) {
            return ret;
        }
        Class<?> serverClass = server.getClass();
        while (!serverClass.getPackage().getName().startsWith(CRAFTBUKKIT_PACKAGE) ) {
            serverClass = serverClass.getSuperclass();
            if (serverClass == null) {
                unsupportedVersion(server);
            }
        }
        String pkg  = serverClass.getPackage().getName();
        int i = pkg.lastIndexOf(".");
        if (i == -1) {
            unsupportedVersion(server);
        }
        String childPackage = pkg.substring(i+1);
        String className = type.getClass().getPackage().getName() + '.' + childPackage + '.' + type.getSimpleName();
        System.out.println("Loading class: " + className);
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
            unsupportedVersion(server);
        } catch (InstantiationException ex) {
            unsupportedVersion(server);
        } catch (IllegalAccessException ex) {
            unsupportedVersion(server);
        } catch (IllegalArgumentException ex) {
            unsupportedVersion(server);
        } catch (InvocationTargetException ex) {
            unsupportedVersion(server);
        }
        // unreached, stupid compiler.
        return null;
    }

    private static void unsupportedVersion(Server server) {
        throw new UnsupportedOperationException("Unsupported CraftBukkit version: " + server.getBukkitVersion());
    }
    
}
