package com.taixue.xiaomingbot.host;

import com.taixue.xiaomingbot.api.command.CommandManager;
import com.taixue.xiaomingbot.api.command.CommandSender;
import com.taixue.xiaomingbot.api.group.Group;
import com.taixue.xiaomingbot.api.group.GroupManager;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractorManager;
import com.taixue.xiaomingbot.api.permission.PermissionSystem;
import com.taixue.xiaomingbot.host.command.executor.CoreCommandExecutor;
import com.taixue.xiaomingbot.host.command.executor.PermissionCommandExecutor;
import com.taixue.xiaomingbot.host.command.sender.ConsoleCommandSender;
import com.taixue.xiaomingbot.host.plugin.PluginManager;
import com.taixue.xiaomingbot.util.EmojiManager;
import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.bot.Bot;
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
        protected static XiaomingBot INSTANCE;
    }

    public XiaomingBot() {
        Holder.INSTANCE = this;
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

    protected EmojiManager emojiManager = new EmojiManager(EMOJI_DIRECTORY);

    protected GroupManager groupManager = GroupManager.forFile(new File(CONFIG_DIRECTORY, "groups.json"));

    public void reloadGroupManager() {
        groupManager = GroupManager.forFile(new File(CONFIG_DIRECTORY, "groups.json"));
    }

    public void reloadPermissionSystem() {
        permissionSystem = PermissionSystem.forFile(new File(CONFIG_DIRECTORY, "permissions.json"));
    }

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
    public PermissionSystem getPermissionSystem() {
        return permissionSystem;
    }

    @Override
    public EmojiManager getEmojis() {
        return emojiManager;
    }

    @Override
    public GroupManager getGroupManager() {
        return groupManager;
    }

    @Override
    public void post(@NotNull SimbotContext context) {
        XiaomingBot instance = getInstance();
        registerCoreCommandExecutor();
        instance.getPluginManager().loadAllPlugins(instance.commandSender);

        Bot defaultBot = context.getBotManager().getDefaultBot();
        Sender sender = defaultBot.getSender().SENDER;

        for (Group test : groupManager.getGroups("test")) {
            sender.sendGroupMsg(test.getCode(), "小明已启动 (๑•̀ㅂ•́)و✧");
        }
    }

    @Override
    public void pre(@NotNull Configuration config) {}

    public void registerCoreCommandExecutor() {
        commandManager.register(new CoreCommandExecutor(), null);
        commandManager.register(new PermissionCommandExecutor(), null);
    }

    public static void main(String[] args) {
        SimbotApp.run(XiaomingBot.class, args);
    }
}
