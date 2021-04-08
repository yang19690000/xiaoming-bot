package com.taixue.xiaomingbot.api.bot;

import com.alibaba.fastjson.JSON;
import com.taixue.xiaomingbot.api.command.CommandSender;
import com.taixue.xiaomingbot.api.plugin.PluginProperty;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 小明的插件加载器加载插件的方法是，先收集所有插件文件，维护一张加载序列表，然后按照表逐一加载。
 * 首先加载没有前置插件的基本插件，然后加载已齐备所有前置插件的插件，不断执行直到两次加载结果相等。
 * 如果最终加载插件数和插件文件总数相等，所有插件均加载成功，否则有的插件加载失败。
 * @author Chuanwise
 */
public interface PluginManager {
    /**
     * 表示一个插件的数据结构
     */
    class PluginLoader {
        protected final File file;
        protected final PluginProperty property;
        protected XiaomingPlugin plugin;

        public PluginLoader(File file, PluginProperty property) {
            this.file = file;
            this.property = property;
        }

        public File getFile() {
            return file;
        }

        public PluginProperty getProperty() {
            return property;
        }

        public XiaomingPlugin getPlugin() {
            return plugin;
        }

        public void setPlugin(XiaomingPlugin plugin) {
            this.plugin = plugin;
        }
    }

    /**
     * 判断某插件是否加载失败
     * @param pluginName
     * @return
     */
    boolean isFail(String pluginName);

    @Nullable
    XiaomingPlugin getPlugin(String pluginName);
}