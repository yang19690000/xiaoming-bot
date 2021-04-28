package com.taixue.xiaoming.bot.core.plugin;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import com.taixue.xiaoming.bot.api.plugin.HookHolder;
import com.taixue.xiaoming.bot.api.plugin.PluginProperty;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.util.FileUtil;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;
import com.taixue.xiaoming.bot.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 小明插件的主类
 * @author Chuanwise
 */
public class XiaomingPluginImpl extends HostObjectImpl implements XiaomingPlugin {
    private PluginProperty property;
    private ClassLoader classLoader;
    private File dataFolder;
    private File configFile;

    @Override@NotNull
    public final File getDataFolder() {
        if (Objects.isNull(dataFolder)) {
            dataFolder = new File(PathUtil.PLUGIN_DIR, getName());
        }
        return dataFolder;
    }

    @Override public final void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override@NotNull
    public final ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override@NotNull
    public final PluginProperty getProperty() {
        return property;
    }

    @Override public final void setProperty(PluginProperty property) {
        this.property = property;
        property.setPlugin(this);
    }

    @Override@NotNull
    public final String getName() {
        return property.getName();
    }

    @Override@NotNull
    public final String getVersion() {
        return property.getVersion();
    }

    @Override@NotNull
    public final String getCompleteName() {
        return getName() + "（" + getVersion() + "）";
    }

    @Override@NotNull
    public final File getConfigFile() {
        if (Objects.isNull(configFile)) {
            configFile = new File(getDataFolder(), "config.json");
        }
        return configFile;
    }

    @Override@Nullable
    public <T extends FileSavedData> T loadConfigAs(@NotNull final Class<T> clazz)
            throws IOException {
        return getXiaomingBot().getFileSavedDataFactory().forFileThrowsException(getConfigFile(), clazz);
    }

    /**
     * 以图形式加载配置文件
     */
    @Override@Nullable
    public Map loadConfigAsMap() throws IOException {
        Map result = null;
        try (FileInputStream inputStream = new FileInputStream(getConfigFile())) {
            result = JsonSerializerUtil.getInstance().readValue(inputStream, Map.class);
        }
        return result;
    }

    /**
     * 将资源文件复制到指定的某处
     */
    @Override public boolean copyResourceTo(@NotNull final String path,
                                  @NotNull final File to)
            throws IOException {
        final InputStream inputStream = getClassLoader().getResourceAsStream(path);
        if (Objects.nonNull(inputStream)) {
            to.getParentFile().mkdirs();
            return FileUtil.copyResource(inputStream, to);
        } else {
            return false;
        }
    }

    /**
     * 如果插件文件夹中不存在配置文件，将资源文件 config.json 复制到该处
     */
    @Override public boolean copyDefaultConfig()
            throws IOException {
        final File configFile = getConfigFile();
        if (configFile.isFile()) {
            return false;
        } else {
            return copyResourceTo("config.json", configFile);
        }
    }

    private Map<String, HookHolder> hookHolders = new HashMap<>();

    @Override@Nullable
    public HookHolder getHookHolder(String otherPluginName) {
        return hookHolders.get(otherPluginName);
    }

    public Map<String, HookHolder> getHookHolders() {
        return hookHolders;
    }

    /**
     * 被别的插件主动脱钩时的操作
     */
    @Override public void onUnhook(XiaomingPlugin plugin, HookHolder holder) {}

    @Override public boolean isHookingWith(String pluginName) {
        return hookHolders.containsKey(pluginName);
    }

    @Override public boolean isHookingWith(XiaomingPlugin plugin) {
        return isHookingWith(plugin.getName());
    }

    /**
     * 主动与其他插件脱钩时的操作
     * @param plugin
     */
    @Override public final boolean unhook(XiaomingPlugin plugin) throws Exception {
        HookHolder hookHolder = getHookHolder(plugin.getName());
        if (Objects.nonNull(hookHolder)) {
            unhook(hookHolder);
            return true;
        }
        else {
            return false;
        }
    }

    @Override public final boolean unhook(HookHolder hookHolder) throws Exception {
        XiaomingPlugin otherPlugin = hookHolder.getOtherPlugin(this);
        getLogger().info("主动和{}解钩", otherPlugin.getCompleteName());
        otherPlugin.onUnhook(this, hookHolder);
        hookHolders.remove(hookHolder);
        otherPlugin.getHookHolders().remove(getName());
        return true;
    }

    @Override public final boolean unhook(String pluginName) throws Exception {
        HookHolder hook = getHookHolder(pluginName);
        if (Objects.nonNull(hook)) {
            return unhook(hook.getSponsor());
        }
        else {
            return false;
        }
    }

    @Override public final boolean unHookAll() throws Exception {
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
    @Override public void onHook(XiaomingPlugin otherPlugin, HookHolder holder) {}

    /**
     * 主动和其他插件挂钩
     */
    @Override@Nullable
    public <T extends HookHolder> T hook(XiaomingPlugin otherPlugin, Class<T> holderClass)
            throws Exception {
        if (!isHookingWith(otherPlugin.getName())) {
            Constructor<T> constructor = holderClass.getConstructor(XiaomingPlugin.class, XiaomingPlugin.class);
            T hookHolder = constructor.newInstance(this, otherPlugin);
            otherPlugin.getHookHolders().put(otherPlugin.getName(), hookHolder);
            otherPlugin.onHook(this, hookHolder);
            return hookHolder;
        }
        else {
            return null;
        }
    }

    @Override@Nullable
    public <T extends HookHolder> T hook(String pluginName, Class<T> holderClass)
            throws Exception {
        XiaomingPlugin plugin = getXiaomingBot().getPluginManager().getPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            return hook(plugin, holderClass);
        }
        else {
            return null;
        }
    }

    /**
     * 最简单的交互器
     * @param user 交互的成员
     * @return 是否需要尝试下一个交互器
     */
    @Override public boolean onMessage(DispatcherUser user) {
        return false;
    }

    @Override public void onEnable() {}

    @Override public void onDisable() {}
}
