package com.taixue.xiaomingbot.test;

import sun.misc.ClassLoaderUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class URLClassLoaderTest {
    public static void main(String[] args) {
        ClassLoader classLoader = URLClassLoaderTest.class.getClassLoader();
        URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
        URL[] urls = Arrays.copyOf(urlClassLoader.getURLs(), urlClassLoader.getURLs().length + 1);
        try {
            File file = new File("E:\\software engineer\\Java\\XiaomingBot\\plugins\\xiaomingbot-lexicons-1.0.jar");
            urls[urls.length - 1] = file.toURL();
            urlClassLoader = URLClassLoader.newInstance(urls, classLoader);
            System.out.println(Class.forName("com.taixue.xiaominglexicons.XiaomingLexicons", true, urlClassLoader));
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Class Not Found!");
        }
    }
}
