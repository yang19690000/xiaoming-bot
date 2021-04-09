package com.taixue.xiaomingbot.api.plugin;

import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.api.listener.userdata.GroupInteractorUserData;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateInteractorUserData;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class XiaomingPlugin {
    protected XiaomingBot xiaomingBot;
    protected String name;
    protected String version;
    protected Logger logger;
    protected File dataFolder;

    protected Map<String, HookHolder> hookRecipients = new HashMap<>();
    protected Map<String, HookHolder> hookSponsors = new HashMap<>();

    public Map<String, HookHolder> getHookRecipients() {
        return hookRecipients;
    }

    public Map<String, HookHolder> getHookSponsors() {
        return hookSponsors;
    }

    @Nullable
    public HookHolder getRecipentHookHolder(String recipientName) {
        return hookRecipients.get(recipientName);
    }

    @Nullable
    public HookHolder getSponsorHookHolder(String sponsorName) {
        return hookSponsors.get(sponsorName);
    }

    public boolean isHookSponsor(String sponsorName) {
        return Objects.nonNull(getSponsorHookHolder(sponsorName));
    }

    public boolean isHookRecipient(String recipientName) {
        return Objects.nonNull(getRecipentHookHolder(recipientName));
    }

    /**
     * 交互器部分
     */
    public boolean isGroupInteractor(String input) {
        return false;
    }

    public void onGroupMessage(GroupInteractorUserData userData, MsgSender msgSender) {}

    public void onPrivateMessage(PrivateInteractorUserData userData, MsgSender msgSender) {}

    public boolean isPrivateInteractor(String input) {
        return false;
    }

    /**
     * 被别的插件主动脱钩时的操作
     */
    public void onUnhook(XiaomingPlugin plugin) {
        HookHolder hook = getSponsorHookHolder(plugin.getName());
        if (Objects.nonNull(hook)) {
            logger.info("被 {} 主动解钩", plugin.getName());
            hookSponsors.remove(plugin.getName());
        }
    }

    public boolean isHookingWith(String pluginName) {
        return isHookRecipient(pluginName) || isHookSponsor(pluginName);
    }

    /**
     * 主动与其他插件脱钩时的操作
     * @param plugin
     */
    public void unhook(XiaomingPlugin plugin) {
        HookHolder hook = getRecipentHookHolder(plugin.getName());
        if (Objects.nonNull(hook)) {
            logger.info("主动和 {} 解钩", plugin.getName());
            hookRecipients.remove(plugin.getName());
            hook.recipient.onUnhook(this);
        }
    }

    public void unhook(String pluginName) {
        HookHolder hook = getRecipentHookHolder(pluginName);
        if (Objects.nonNull(hook)) {
            unhook(hook.getSponsor());
        }
    }

    public void onHook(XiaomingPlugin plugin, HookHolder holder) {
        hookRecipients.put(plugin.name, holder);
    }

    @Nullable
    public <T extends HookHolder> T hook(XiaomingPlugin plugin, Class<T> holderClass)
            throws Exception {
        if (!isHookingWith(plugin.getName())) {
            Constructor<T> constructor = holderClass.getConstructor(XiaomingPlugin.class, XiaomingPlugin.class);
            T holder = constructor.newInstance(this, plugin);
            plugin.hookSponsors.put(name, holder);
            plugin.onHook(this, holder);
            return holder;
        }
        return null;
    }

    @Nullable
    public <T extends HookHolder> T hook(String pluginName, Class<T> holderClass)
            throws Exception {
        XiaomingPlugin plugin = xiaomingBot.getPluginManager().getPlugin(pluginName);
        if (Objects.nonNull(plugin) && !isHookingWith(pluginName)) {
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
        return name + " " + version;
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
}
