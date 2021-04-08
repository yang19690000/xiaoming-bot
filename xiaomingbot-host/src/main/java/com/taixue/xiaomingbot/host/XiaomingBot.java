package com.taixue.xiaomingbot.host;

import com.taixue.xiaomingbot.api.command.CommandSender;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractorManager;
import com.taixue.xiaomingbot.host.commandsender.ConsoleCommandSender;
import com.taixue.xiaomingbot.host.plugin.PluginManager;
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

    protected File pluginDirectory = new File("plugins");

    protected PluginManager pluginManager = new PluginManager(pluginDirectory);

    protected CommandSender commandSender = new ConsoleCommandSender();

    protected PrivateInteractorManager privateInteractorManager = new PrivateInteractorManager();

    protected GroupInteractorManager groupInteractorManager = new GroupInteractorManager();

    private void createDirectories() {
        pluginDirectory.mkdirs();
    }

    public static XiaomingBot getInstance() {
        return Holder.INSTANCE;
    }

    public File getPluginDirectory() {
        return pluginDirectory;
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
    public void post(@NotNull SimbotContext context) {
        XiaomingBot instance = getInstance();
        instance.createDirectories();
        instance.getPluginManager().loadAllPlugins(instance.commandSender);
    }

    @Override
    public void pre(@NotNull Configuration config) {}

    public static void main(String[] args) {
        SimbotApp.run(XiaomingBot.class, args);
    }
}
