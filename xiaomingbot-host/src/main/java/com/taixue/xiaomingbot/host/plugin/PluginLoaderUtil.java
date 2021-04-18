package com.taixue.xiaomingbot.host.plugin;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

/**
 * 插件加载器
 * @author Chuanwise
 */
public class PluginLoaderUtil {
    /**
     * 设立某个 jarFile 的 URLClassLoader
     * @param jarFile
     * @return
     */
    @Nullable
    public static URLClassLoader urlClassLoader(final File jarFile) {
        if (jarFile.exists() && jarFile.getName().endsWith(".jar")) {
            try {
                URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{jarFile.toURL()},
                        PluginLoaderUtil.class.getClassLoader());
                return urlClassLoader;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }

    public static <T> T loadClass(final URLClassLoader urlClassLoader,
                                  final String className,
                                  final Class<T> clazz)
            throws ClassNotFoundException, ClassCastException {
        return ((T) urlClassLoader.loadClass(className));
    }

    @Nullable
    public static Class loadClass(final File jarFile,
                                  final String className)
            throws ClassNotFoundException, ClassCastException {
        if (!jarFile.exists()) {
            return null;
        }
        URLClassLoader urlClassLoader = urlClassLoader(jarFile);
        if (Objects.isNull(urlClassLoader)) {
            return null;
        }
        return urlClassLoader.loadClass(className);
    }

    @Nullable
    public static <T> T loadPluginInstance(final File jarFile, final String className, final Class<T> pluginClass)
        throws ClassNotFoundException, ClassCastException, InstantiationException, IllegalAccessException {
        Class aClass = loadClass(jarFile, className);
        if (Objects.isNull(aClass) ||
                !pluginClass.isAssignableFrom(aClass)) {
            return null;
        }
        return ((T) aClass.newInstance());
    }
}
