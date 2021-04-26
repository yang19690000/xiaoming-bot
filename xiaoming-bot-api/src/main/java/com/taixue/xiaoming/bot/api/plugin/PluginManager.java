package com.taixue.xiaoming.bot.api.plugin;

import com.alibaba.fastjson.JSON;
import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;
import com.taixue.xiaoming.bot.util.PluginLoaderUtil;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 小明的插件加载器加载插件的方法是，先收集所有插件文件，维护一张加载序列表，然后按照表逐一加载。
 * 首先加载没有前置插件的基本插件，然后加载已齐备所有前置插件的插件，不断执行直到两次加载结果相等。
 * 如果最终加载插件数和插件文件总数相等，所有插件均加载成功，否则有的插件加载失败。
 */
public class PluginManager extends HostObject {
    public final File directory;

    public PluginManager(File directory) {
        this.directory = directory;
    }

    private BidiMap pluginBidiMap = new DualHashBidiMap();

    private Set<XiaomingPlugin> loadedPlugins = new HashSet<>();

    public boolean isLoaded(String pluginName) {
        for (XiaomingPlugin loadedPlugin : loadedPlugins) {
            if (Objects.equals(loadedPlugin.getName(), pluginName)) {
                return true;
            }
        }
        return false;
    }

    public Set<XiaomingPlugin> getLoadedPlugins() {
        return loadedPlugins;
    }

    /**
     * 加载一个插件（无参数校验，确认其无问题）
     */
    public boolean loadPlugin(final XiaomingUser sender,
                              final XiaomingPlugin.Property property) {
        XiaomingPlugin plugin = null;
        final File pluginFile = property.getPluginFile();

        try {
            plugin = PluginLoaderUtil.loadPluginInstance(pluginFile, property.getMain(), XiaomingPlugin.class);
        } catch (ClassCastException classCastException) {
            sender.sendError("插件主类：{} 不是 {} 的子类", property.getMain(), XiaomingPlugin.class.getName());
            return false;
        } catch (Exception exception) {
            sender.sendError("加载插件 {} 的主类 {} 时出现异常：{}", property.get("name"), property.getMain(), exception);
            exception.printStackTrace();
            return false;
        }

        plugin.setProperty(property);
        plugin.setClassLoader(PluginLoaderUtil.urlClassLoader(pluginFile));

        if (enablePlugin(sender, plugin)) {
            loadedPlugins.add(plugin);
        }
        return true;
    }

    public void loadAllPlugins(final XiaomingUser user) {
        // 本次需要加载的插件
        pushAllUnloadLoader(user);

        if (pluginBidiMap.isEmpty()) {
            user.sendMessage("没有本次需要加载的插件");
            return;
        }

        // 不断循环，直到无法再加载插件为止
        int loadedPluginNumber = loadedPlugins.size();
        int lastLoadedPluginNumber;
        do {
            lastLoadedPluginNumber = loadedPlugins.size();
            for (XiaomingPlugin.Property value : ((Set<XiaomingPlugin.Property>) pluginBidiMap.values())) {
                tryLoadPlugin(user, value);
            }
        } while (lastLoadedPluginNumber != loadedPlugins.size());

        user.sendMessage("成功加载了 {} 个插件", loadedPlugins.size() - loadedPluginNumber);
    }

    public boolean tryLoadPlugin(final XiaomingUser sender,
                                 final XiaomingPlugin.Property property) {
        if (isLoaded(property.getName())) {
            return false;
        }
        boolean allFrontLoaded = true;
        final Object frontsObject = property.get("fronts");
        if (frontsObject instanceof List) {
            try {
                final List<String> fronts = (List<String>) frontsObject;
                for (String frontPluginName : fronts) {
                    if (!isLoaded(frontPluginName)) {
                        allFrontLoaded = false;
                        break;
                    }
                }
            } catch (ClassCastException exception) {
                exception.printStackTrace();
                sender.sendError("插件属性文件中的 fronts 应该是前置插件名数组");
                return false;
            }
        }

        if (allFrontLoaded) {
            try {
                return loadPlugin(sender, property);
            } catch (Exception e) {
                sender.sendError("加载插件：{} 时出现异常：{}", property.getName(), e);
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    public XiaomingPlugin getPlugin(final String pluginName) {
        for (XiaomingPlugin loadedPlugin : loadedPlugins) {
            if (Objects.equals(pluginName, loadedPlugin.getName())) {
                return loadedPlugin;
            }
        }
        return null;
    }

    public boolean unloadPlugin(final XiaomingUser sender, String pluginName) {
        XiaomingPlugin plugin = getPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            unloadPlugin(sender, plugin);
            return true;
        } else {
            return false;
        }
    }

    public void reloadAll(final XiaomingUser sender) {
        for (XiaomingPlugin loadedPlugin : loadedPlugins) {
            unloadPlugin(sender, loadedPlugin);
        }
    }

    public boolean reloadPlugin(final XiaomingUser sender,
                                final String pluginName) {
        if (!isLoaded(pluginName)) {
            return false;
        }
        final XiaomingPlugin.Property property = (XiaomingPlugin.Property) pluginBidiMap.get(pluginName);
        return Objects.nonNull(property) && reloadPlugin(sender, property);
    }

    public boolean reloadPlugin(final XiaomingUser user,
                                final XiaomingPlugin.Property property) {
        final XiaomingPlugin plugin = property.getPlugin();
        unloadPlugin(user, plugin);
        return tryLoadPlugin(user, property);
    }

    public void disablePlugin(final XiaomingUser user,
                              final XiaomingPlugin plugin) {
        try {
            user.sendMessage("正在卸载插件：{}", plugin.getName());
            plugin.onDisable();
            user.sendMessage("插件 {} 卸载完成", plugin.getName());
        } catch (Exception exception) {
            user.sendError("卸载插件 {} 时出现异常：{}", plugin.getName(), exception);
            exception.printStackTrace();
        }
    }

    public boolean enablePlugin(final XiaomingUser user,
                                final XiaomingPlugin plugin) {
        try {
            plugin.onEnable();
            user.sendMessage("插件 {} 初始化完成", plugin.getName());
            return true;
        } catch (Exception exception) {
            user.sendError("初始化插件 {} 时出现异常：{}", plugin.getName(), exception);
            exception.printStackTrace();
            return false;
        }
    }

    public void unloadPlugin(final XiaomingUser user,
                             final XiaomingPlugin plugin) {
        disablePlugin(user, plugin);
        loadedPlugins.remove(plugin.getName());
        try {
            plugin.unHookAll();
        } catch (Exception exception) {
            user.sendError("和插件脱钩时出现异常：{}，相关插件可能无法正常运行。", exception);
            exception.printStackTrace();
        }
        getXiaomingBot().getInteractorManager().getPluginInteractors().remove(plugin);
        getXiaomingBot().getCommandManager().getPluginCommandExecutors().remove(plugin);
    }

    /**
     * 获取一个插件的 plugin.json
     * @param jarFile
     * @return 如果插件内无 plugin.json，则返回 null
     * @throws IOException
     */
    @Nullable
    public XiaomingPlugin.Property getPluginProperty(final JarFile jarFile) throws IOException {
        // 获取插件属性 plugin.json
        ZipEntry entry = jarFile.getEntry("plugin.json");
        if (Objects.isNull(entry)) {
            return null;
        }

        XiaomingPlugin.Property pluginProperty = null;
        try (InputStream inputStream = jarFile.getInputStream(entry);) {
            pluginProperty = (XiaomingPlugin.Property) JSON.parseObject(inputStream, XiaomingPlugin.Property.class);
        }
        return pluginProperty;
    }

    @Nullable
    public XiaomingPlugin.Property getPluginProperty(final File pluginFile) throws IOException {
        return getPluginProperty(new JarFile(pluginFile));
    }

    public void pushAllUnloadLoader(final XiaomingUser user) {
        for (File pluginFile : directory.listFiles()) {
            if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
                try {
                    final JarFile jarFile = new JarFile(pluginFile);
                    final JarEntry pluginPropertyEntry = jarFile.getJarEntry("plugin.json");
                    if (Objects.isNull(pluginPropertyEntry)) {
                        user.sendError("没有在插件文件 {} 中找到插件属性文件", pluginFile.getName());
                    } else {
                        final InputStream inputStream = jarFile.getInputStream(pluginPropertyEntry);
                        final XiaomingPlugin.Property property = JsonSerializerUtil.getInstance().getObjectMapper().readValue(inputStream, XiaomingPlugin.Property.class);
                        property.setPluginFile(pluginFile);
                        pluginBidiMap.put(property.getName(), property);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (pluginFile.isFile()) {
                getLogger().error("插件文件夹：" + directory.getAbsolutePath() + " 中出现了非 jar 类型的文件：" + pluginFile.getName());
            }
        }
    }
}