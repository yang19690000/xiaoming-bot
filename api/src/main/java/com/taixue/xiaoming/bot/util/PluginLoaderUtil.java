package com.taixue.xiaoming.bot.util;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;

/**
 * 插件类加载器
 * @author Chuanwise
 */
public class PluginLoaderUtil {
    /**
     * 设立某个 jarFile 的 URLClassLoader
     * @param jarFile
     * @return
     */
    public static ClassLoader urlClassLoader(final File jarFile,
                                             final ClassLoader father)
            throws MalformedURLException {
        return URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()}, father);
    }

    /**
     * 在一个 URL 类加载器中增加一个 URL。如果这个类加载器中已经有这个 URL，则本操作无影响
     * @param jarFile
     * @return
     */
    public static URLClassLoader extendURLClassLoader(final File jarFile,
                                                      final URLClassLoader father)
            throws Exception {
        final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        addURL.invoke(father, jarFile.toURI().toURL());
        addURL.setAccessible(false);
        return father;
    }


    @Nullable
    public static Class loadClass(final File jarFile, final String className, final ClassLoader classLoader)
            throws Exception {
        return extendURLClassLoader(jarFile, ((URLClassLoader) classLoader)).loadClass(className);
    }
}