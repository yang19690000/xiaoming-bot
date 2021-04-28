package com.taixue.xiaoming.bot.api.plugin;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.api.data.FileSavedData;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface XiaomingPlugin extends HostObject {
    @NotNull
    File getDataFolder();

    void setClassLoader(ClassLoader classLoader);

    @NotNull
    ClassLoader getClassLoader();

    @NotNull
    PluginProperty getProperty();

    void setProperty(PluginProperty property);

    @NotNull
    String getName();

    @NotNull
    String getVersion();

    @NotNull
    String getCompleteName();

    @NotNull
    File getConfigFile();

    @Nullable
    <T extends FileSavedData> T loadConfigAs(@NotNull Class<T> clazz)
            throws IOException;

    @Nullable
    Map loadConfigAsMap() throws IOException;

    boolean copyResourceTo(@NotNull String path,
                           @NotNull File to)
            throws IOException;

    boolean copyDefaultConfig()
                    throws IOException;

    @Nullable
    HookHolder getHookHolder(String otherPluginName);

    @NotNull
    Map<String, HookHolder> getHookHolders();

    void onUnhook(XiaomingPlugin plugin, HookHolder holder);

    boolean isHookingWith(String pluginName);

    boolean isHookingWith(XiaomingPlugin plugin);

    boolean unhook(XiaomingPlugin plugin) throws Exception;

    boolean unhook(HookHolder hookHolder) throws Exception;

    boolean unhook(String pluginName) throws Exception;

    boolean unHookAll() throws Exception;

    void onHook(XiaomingPlugin otherPlugin, HookHolder holder);

    @Nullable
    <T extends HookHolder> T hook(XiaomingPlugin otherPlugin, Class<T> holderClass)
            throws Exception;

    @Nullable
    <T extends HookHolder> T hook(String pluginName, Class<T> holderClass)
            throws Exception;

    boolean onMessage(DispatcherUser user);

    void onEnable();

    void onDisable();
}
