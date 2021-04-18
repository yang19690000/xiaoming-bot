package com.taixue.xiaomingbot.api.plugin;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUser;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUser;
import com.taixue.xiaomingbot.util.FileDataFactory;
import com.taixue.xiaomingbot.util.FileUtil;
import com.taixue.xiaomingbot.util.JSONFileData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class XiaomingPlugin {
    private static final String CONFIG_FILE_NAME = "config.json";

    private XiaomingBot xiaomingBot;
    private String name;
    private String version;
    private Logger logger;
    private File dataFolder;
    private ClassLoader classLoader;

    private Map<String, HookHolder> hookHolders = new HashMap<>();

    public Map<String, HookHolder> getHookHolders() {
        return hookHolders;
    }

    public File getConfigFile() {
        return new File(getDataFolder(), CONFIG_FILE_NAME);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 加载配置文件为某特定类型
     */
    @Nullable
    public <T extends JSONFileData> T loadConfigAs(Class<T> configClass)
            throws IOException, JSONException {
        return JSONFileData.forFile(getConfigFile(), configClass);
    }

    public <T extends JSONFileData> T loadConfigAsOrNew(Class<T> configClass, FileDataFactory<T> factory) {
        return JSONFileData.forFileOrNew(getConfigFile(), configClass, factory);
    }

    /**
     * 以图形式加载配置文件
     */
    @Nullable
    public Map<String, Object> loadConfigAsMap() throws IOException, JSONException {
        File file = getConfigFile();
        if (file.exists() && !file.isDirectory()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                return JSON.parseObject(fileInputStream, Map.class);
            }
        }
        return null;
    }

    /**
     * 将资源文件 config.json 复制到插件数据文件夹中
     * @return
     */
    public boolean copyDefaultConfig() {
        File file = getConfigFile();
        if (!file.exists() || file.isDirectory()) {
            try {
                final InputStream inputStream = getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
                if (Objects.nonNull(inputStream)) {
                    FileUtil.copyResource(inputStream, file);
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Nullable
    public HookHolder getHookHolder(String otherPluginName) {
        return hookHolders.get(otherPluginName);
    }

    /**
     * 被别的插件主动脱钩时的操作
     */
    public void onUnhook(XiaomingPlugin plugin, HookHolder holder) {}

    public boolean isHookingWith(String pluginName) {
        return hookHolders.containsKey(pluginName);
    }

    public boolean isHookingWith(XiaomingPlugin plugin) {
        return isHookingWith(plugin.getName());
    }

    /**
     * 主动与其他插件脱钩时的操作
     * @param plugin
     */
    public final boolean unhook(XiaomingPlugin plugin) throws Exception {
        HookHolder hookHolder = getHookHolder(plugin.getName());
        if (Objects.nonNull(hookHolder)) {
            unhook(hookHolder);
            return true;
        }
        else {
            return false;
        }
    }

    public final boolean unhook(HookHolder hookHolder) throws Exception {
        XiaomingPlugin otherPlugin = hookHolder.getOtherPlugin(this);
        logger.info("主动和{}解钩", otherPlugin.getCompleteName());
        otherPlugin.onUnhook(this, hookHolder);
        hookHolders.remove(hookHolder);
        otherPlugin.hookHolders.remove(name);
        return true;
    }

    public final boolean unhook(String pluginName) throws Exception {
        HookHolder hook = getHookHolder(pluginName);
        if (Objects.nonNull(hook)) {
            return unhook(hook.getSponsor());
        }
        else {
            return false;
        }
    }

    public final boolean unHookAll() throws Exception {
        for (Map.Entry<String, HookHolder> entry : hookHolders.entrySet()) {
            if (!unhook(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 被正在挂钩的插件解钩时的操作
     * @param otherPlugin
     * @param holder
     */
    public void onHook(XiaomingPlugin otherPlugin, HookHolder holder) {}

    /**
     * 主动和其他插件挂钩
     */
    @Nullable
    public <T extends HookHolder> T hook(XiaomingPlugin otherPlugin, Class<T> holderClass)
            throws Exception {
        if (!isHookingWith(otherPlugin.getName())) {
            Constructor<T> constructor = holderClass.getConstructor(XiaomingPlugin.class, XiaomingPlugin.class);
            T hookHolder = constructor.newInstance(this, otherPlugin);
            otherPlugin.hookHolders.put(otherPlugin.name, hookHolder);
            otherPlugin.onHook(this, hookHolder);
            return hookHolder;
        }
        else {
            return null;
        }
    }

    @Nullable
    public <T extends HookHolder> T hook(String pluginName, Class<T> holderClass)
            throws Exception {
        XiaomingPlugin plugin = xiaomingBot.getPluginManager().getPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            return hook(plugin, holderClass);
        }
        else {
            return null;
        }
    }

    public XiaomingBot getXiaomingBot() {
        return xiaomingBot;
    }

    public void setXiaomingBot(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;
    }

    public String getCompleteName() {
        return name + " (" + version + ")";
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getVersion() {
        return version;
    }

    public void onEnable() {}

    public void onDisable() {}

    public void setDataFolder(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public boolean onGroupMessage(GroupDispatcherUser userData) {
        return false;
    }

    public boolean onPrivateMessage(PrivateDispatcherUser userData) {
        return false;
    }
}
