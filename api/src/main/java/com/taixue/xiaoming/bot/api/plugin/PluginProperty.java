package com.taixue.xiaoming.bot.api.plugin;

import java.io.File;

public interface PluginProperty {
    XiaomingPlugin getPlugin();

    void setPlugin(XiaomingPlugin plugin);

    File getPluginFile();

    void setPluginFile(File pluginFile);

    String getMain();

    String getName();

    String getVersion();

    Object get(String key);

    <T> T get(String key, Class<T> clazz);
}
