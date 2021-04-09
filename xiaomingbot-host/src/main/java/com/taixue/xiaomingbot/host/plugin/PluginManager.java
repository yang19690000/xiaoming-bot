package com.taixue.xiaomingbot.host.plugin;

import com.alibaba.fastjson.JSON;
import com.taixue.xiaomingbot.api.plugin.PluginProperty;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaomingbot.api.command.CommandSender;
import com.taixue.xiaomingbot.host.XiaomingBot;
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
 */
public class PluginManager implements com.taixue.xiaomingbot.api.bot.PluginManager {
    public static final Logger LOGGER = LoggerFactory.getLogger("PluginLoader");
    public final File directory;

    public PluginManager(File directory) {
        this.directory = directory;
    }

    protected Map<String, PluginLoader> pluginFiles = new HashMap<>();

    protected Map<String, PluginLoader> failToLoadPlugins = new HashMap<>();

    protected Map<String, PluginLoader> loadedPlugins = new HashMap<>();

    protected List<PluginLoader> pluginLoaderList = new ArrayList<>();

    public boolean isLoaded(String pluginName) {
        return loadedPlugins.containsKey(pluginName);
    }

    public void registerLoadedPlugin(final File from,
                                     final PluginProperty property,
                                     final XiaomingPlugin plugin) {
        PluginLoader pluginLoader = new PluginLoader(from, property);
        pluginLoader.setPlugin(plugin);
        loadedPlugins.put(plugin.getName(), pluginLoader);
    }

    /**
     * 设置某插件加载失败
     * @param pluginName
     */
    public void registerFailToLoadedPlugin(final File from,
                                           final String pluginName,
                                           @Nullable final PluginProperty property) {
        failToLoadPlugins.put(pluginName, new PluginLoader(from, property));
    }

    /**
     * 判断某插件是否加载失败
     * @param pluginName
     * @return
     */
    @Override
    public boolean isFail(String pluginName) {
        return failToLoadPlugins.containsKey(pluginName);
    }

    public boolean loadPlugin(CommandSender sender, PluginLoader loader)
            throws IOException {
        PluginProperty property = loader.getProperty();
        File pluginFile = loader.getFile();

        if (Objects.isNull(property)) {
            sender.sendError("缺少必要的文件：plugin.json");
            return false;
        }

        if (Objects.isNull(property.getMain())) {
            sender.sendError("plugin.json 文件中缺少必要的 main 属性");
            return false;
        }

        XiaomingPlugin plugin = null;
        try {
            plugin = PluginLoaderUtil.loadPluginInstance(pluginFile, property.getMain(), XiaomingPlugin.class);
            plugin.setName(property.getName());
            plugin.setVersion(property.getVersion());
            plugin.setLogger(LoggerFactory.getLogger(property.getName()));
            plugin.setDataFolder(new File(directory, property.getName()));
            plugin.setXiaomingBot(XiaomingBot.getInstance());

            if (Objects.nonNull(plugin)) {
                registerLoadedPlugin(pluginFile, property, plugin);
                try {
                    LOGGER.info("正在初始化插件：{}", property.getName());
                    plugin.onEnable();
                    LOGGER.info("插件 {} 初始化完成", property.getName());
                    return true;
                }
                catch (Exception exception) {
                    sender.sendError("初始化插件 {} 时出现异常：{}", property.getName(), exception);
                    exception.printStackTrace();
                }
            }
            else {
                sender.sendError("加载插件 {} 时意外遇到了未抛出异常的空指针");
            }
        }
        catch (ClassCastException classCastException) {
            sender.sendError("插件主类：{} 不是 {} 的子类", property.getMain(), XiaomingPlugin.class.getName());
        }
        catch (Exception exception) {
            sender.sendError("加载插件 {} 时出现异常：{}", property.getName(), exception);
        }
        registerFailToLoadedPlugin(pluginFile, property.getName(), property);
        return false;
    }

    public void loadAllPlugins(CommandSender sender) {
        // 本次需要加载的插件数
        List<PluginLoader> loaders = new ArrayList<>();
        pushAllUnloadLoader(directory, loaders);

        if (loaders.isEmpty()) {
            sender.sendMessage("没有本次需要加载的插件");
            return;
        }

        // 本次循环中加载了的插件数。
        int currentLoadNumber;
        int totalLoadedNumber = 0;

        // 不断循环，直到无法再加载插件为止
        do {
            currentLoadNumber = 0;
            for (PluginLoader loader: loaders) {
                PluginProperty property = loader.getProperty();
                if (tryLoadPlugin(sender, loader)) {
                    currentLoadNumber++;
                }
            }
            totalLoadedNumber += currentLoadNumber;
        }
        while (currentLoadNumber != 0);

        if (totalLoadedNumber != loaders.size()) {
            sender.sendMessage("计划加载 {} 个插件，{} 个加载成功，{} 个加载失败",
                    loaders.size(),
                    totalLoadedNumber,
                    loaders.size() - totalLoadedNumber);
            for (PluginLoader loader: loaders) {
                if (isLoaded(loader.getProperty().getName())) {
                    continue;
                }
                if (Objects.nonNull(loader.getProperty().getFronts())) {
                    for (String frontPluginName: loader.getProperty().getFronts()) {
                        if (!isLoaded(frontPluginName)) {
                            sender.sendError("加载插件 {} 需要先加载好前置插件 {}，但前置插件未安装或加载失败。",
                                    loader.getProperty().getName(), frontPluginName);
                        }
                    }
                }
            }
        }
        else {
            sender.sendMessage("成功加载了 {} 个插件", totalLoadedNumber);
        }
    }

    public boolean tryLoadPlugin(CommandSender sender, PluginLoader loader) {
        if (isLoaded(loader.getProperty().getName()) ||
            isFail(loader.getProperty().getName())) {
            return false;
        }
        boolean allFrontLoaded = true;
        if (Objects.nonNull(loader.getProperty().getFronts())) {
            for (String frontPluginName: loader.getProperty().getFronts()) {
                if (!isLoaded(frontPluginName)) {
                    allFrontLoaded = false;
                    break;
                }
            }
        }

        if (allFrontLoaded) {
            try {
                boolean result = loadPlugin(sender, loader);
                return result;
            }
            catch (IOException e) {
                sender.sendError("加载插件：{} 时出现异常：{}", loader.getProperty().getName(), e);
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }

    public PluginProperty fillDefaultValues(File pluginFile,
                                            PluginProperty property) {
        // 若无 name 属性，则将其设置为 jar 文件名
        if (Objects.isNull(property.getName())) {
            property.setName(pluginFile.getName().substring(0, pluginFile.getName().lastIndexOf('.')));
            LOGGER.info("缺少插件名，已将其设置为 " + property.getName());
        }

        // 若无 version 属性，则将其设置为 (unknown-version)
        if (Objects.isNull(property.getVersion())) {
            property.setVersion("(unknown-version)");
            LOGGER.info("缺少插件版本，已将其设置为 " + property.getVersion());
        }

        return property;
    }

    /**
     * 获取一个插件的 plugin.json
     * @param jarFile
     * @return 如果插件内无 plugin.json，则返回 null
     * @throws IOException
     */
    @Nullable
    public PluginProperty pluginProperty(JarFile jarFile) throws IOException {
        // 获取插件属性 plugin.json
        ZipEntry entry = jarFile.getEntry("plugin.json");
        if (Objects.isNull(entry)) {
            return null;
        }

        PluginProperty pluginProperty = null;
        try (InputStream inputStream = jarFile.getInputStream(entry);) {
            pluginProperty = (PluginProperty) JSON.parseObject(inputStream, PluginProperty.class);
        }
        return pluginProperty;
    }

    @Nullable
    public PluginProperty pluginProperty(File pluginFile) throws IOException {
        return pluginProperty(new JarFile(pluginFile));
    }

    public boolean pushUnloadLoader(File pluginFile, List<PluginLoader> loaders) {
        try {
            PluginProperty property = pluginProperty(pluginFile);
            if (Objects.nonNull(property)) {
                property = fillDefaultValues(pluginFile, property);

                if (!isLoaded(property.getName())) {
                    loaders.add(new PluginLoader(pluginFile, property));
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                LOGGER.error("载入 {} 中的插件信息时意外遇到了空的 pluginProperty", pluginFile);
                return false;
            }
        }
        catch (IOException ioException) {
            LOGGER.error("载入 {} 中的插件信息时出现异常：{}", pluginFile, ioException);
            ioException.printStackTrace();
            return false;
        }
    }

    public void pushAllUnloadLoader(File directory, List<PluginLoader> loaders) {
        for (File pluginFile: directory.listFiles()) {
            if (pluginFile.getName().endsWith(".jar")) {
                pushUnloadLoader(pluginFile, loaders);
            }
            else if (!pluginFile.isDirectory()) {
                LOGGER.error("插件文件夹：" + directory.getAbsolutePath() + " 中出现了非 jar 类型的文件：" + pluginFile.getName());
            }
        }
    }

    @Nullable
    @Override
    public XiaomingPlugin getPlugin(String pluginName) {
        return null;
    }
}