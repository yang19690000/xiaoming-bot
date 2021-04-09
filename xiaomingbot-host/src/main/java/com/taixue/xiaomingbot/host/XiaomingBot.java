package com.taixue.xiaomingbot.host;

import com.taixue.xiaomingbot.api.command.CommandManager;
import com.taixue.xiaomingbot.api.command.CommandSender;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractorManager;
import com.taixue.xiaomingbot.api.permission.BasePermissionSystem;
import com.taixue.xiaomingbot.host.commandsender.ConsoleCommandSender;
import com.taixue.xiaomingbot.host.permission.PermissionSystem;
import com.taixue.xiaomingbot.host.plugin.PluginManager;
import com.taixue.xiaomingbot.util.Emojis;
import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;

import java.io.File;
/**
 * @author Chuanwise
 */
@SimbotApplication
public class XiaomingBot
        extends com.taixue.xiaomingbot.api.bot.XiaomingBot
        implements SimbotProcess {
    private static class Holder {
        protected static final XiaomingBot INSTANCE = new XiaomingBot();
    }

    protected static final File PLUGIN_DIRECTORY = new File("plugins");

    protected static final File CONFIG_DIRECTORY = new File("config");

    protected static final File EMOJI_DIRECTORY = new File("emoji");

    static {
        PLUGIN_DIRECTORY.mkdirs();
        CONFIG_DIRECTORY.mkdirs();
        EMOJI_DIRECTORY.mkdirs();
    }

    protected PluginManager pluginManager = new PluginManager(PLUGIN_DIRECTORY);

    protected CommandSender commandSender = new ConsoleCommandSender();

    protected PrivateInteractorManager privateInteractorManager = new PrivateInteractorManager();

    protected GroupInteractorManager groupInteractorManager = new GroupInteractorManager();

    protected CommandManager commandManager = new CommandManager();

    protected PermissionSystem permissionSystem = PermissionSystem.forFile(new File(CONFIG_DIRECTORY, "permissions.json"));

    protected Emojis emojis = new Emojis(EMOJI_DIRECTORY);

    public static XiaomingBot getInstance() {
        return Holder.INSTANCE;
    }

    public File getPluginDirectory() {
        return PLUGIN_DIRECTORY;
    }

    @Override
    public PrivateInteractorManager getPrivateInteractorManager() {
        return privateInteractorManager;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public GroupInteractorManager getGroupInteractorManager() {
        return groupInteractorManager;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public BasePermissionSystem getPermissionSystem() {
        return permissionSystem;
    }

    @Override
    public Emojis getEmojis() {
        return emojis;
    }

    @Override
    public void post(@NotNull SimbotContext context) {
        XiaomingBot instance = getInstance();
        instance.getPluginManager().loadAllPlugins(instance.commandSender);
    }

    @Override
    public void pre(@NotNull Configuration config) {}

    public static void main(String[] args) {
        SimbotApp.run(XiaomingBot.class, args);
    }
}
