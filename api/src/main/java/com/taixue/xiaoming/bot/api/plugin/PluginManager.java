package com.taixue.xiaoming.bot.api.plugin;

import com.taixue.xiaoming.bot.api.base.XiaomingObject;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;

public interface PluginManager extends XiaomingObject {
    boolean isLoaded(String pluginName);

    Set<XiaomingPlugin> getLoadedPlugins();

    /**
     * 加载一个插件（无参数校验，确认其无问题）
     */
    XiaomingPlugin loadPlugin(XiaomingUser sender,
                       PluginProperty property);

    void loadAllPlugins(XiaomingUser user);

    boolean tryLoadPlugin(XiaomingUser sender,
                          PluginProperty property);

    @Nullable
    XiaomingPlugin getPlugin(String pluginName);

    boolean unloadPlugin(XiaomingUser sender, String pluginName);

    void reloadAll(XiaomingUser sender);

    boolean reloadPlugin(XiaomingUser sender,
                         String pluginName);

    boolean reloadPlugin(XiaomingUser sender,
                         XiaomingPlugin plugin);

    boolean reloadPlugin(XiaomingUser user,
                         PluginProperty property);

    void disablePlugin(XiaomingUser user,
                       XiaomingPlugin plugin);

    boolean enablePlugin(XiaomingUser user,
                         XiaomingPlugin plugin);

    void unloadPlugin(XiaomingUser user,
                      XiaomingPlugin plugin);

    /**
     * 获取一个插件的 plugin.json
     * @param jarFile
     * @return 如果插件内无 plugin.json，则返回 null
     * @throws IOException
     */
    @Nullable
    PluginProperty getPluginProperty(JarFile jarFile) throws IOException;

    @Nullable
    PluginProperty getPluginProperty(File pluginFile) throws IOException;

    void pushAllUnloadLoader(XiaomingUser user);
}
