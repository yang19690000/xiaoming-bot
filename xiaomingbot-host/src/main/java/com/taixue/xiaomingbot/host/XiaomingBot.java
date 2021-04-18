package com.taixue.xiaomingbot.host;

import com.taixue.xiaomingbot.api.base.XiaomingConfig;
import com.taixue.xiaomingbot.api.command.CommandManager;
import com.taixue.xiaomingbot.api.command.CommandSender;
import com.taixue.xiaomingbot.api.group.Group;
import com.taixue.xiaomingbot.api.group.GroupManager;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractorManager;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractorManager;
import com.taixue.xiaomingbot.api.permission.PermissionSystem;
import com.taixue.xiaomingbot.api.picture.PictureManager;
import com.taixue.xiaomingbot.api.plugin.PluginConfig;
import com.taixue.xiaomingbot.api.timetask.TimeTaskManager;
import com.taixue.xiaomingbot.api.user.UserManager;
import com.taixue.xiaomingbot.host.command.executor.CoreCommandExecutor;
import com.taixue.xiaomingbot.host.command.executor.PermissionCommandExecutor;
import com.taixue.xiaomingbot.host.command.executor.UserEventCommandExecutor;
import com.taixue.xiaomingbot.host.command.sender.ConsoleCommandSender;
import com.taixue.xiaomingbot.host.plugin.PluginManager;
import com.taixue.xiaomingbot.api.picture.EmojiManager;
import com.taixue.xiaomingbot.host.thread.ConsoleCommandListener;
import com.taixue.xiaomingbot.util.MessageManager;
import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.message.events.GroupMute;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Chuanwise
 */
@SimbotApplication
public class XiaomingBot
        extends com.taixue.xiaomingbot.api.bot.XiaomingBot
        implements SimbotProcess {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static class Holder {
        protected static XiaomingBot INSTANCE;
    }

    public static final int MAX_THREAD_NUMBER = 20;

    public static final ExecutorService SERVICE = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);

    public XiaomingBot() {
        Holder.INSTANCE = this;
    }

    private static final File PLUGIN_DIRECTORY = new File("plugins");

    private static final File CONFIG_DIRECTORY = new File("configs");

    private static final File EMOJI_DIRECTORY = new File("emojis");

    private static final File PICTURE_DIRECTORY = new File("pictures");

    private static final File MESSAGE_DIRECTORY = new File("messages");

    static {
        PLUGIN_DIRECTORY.mkdirs();
        CONFIG_DIRECTORY.mkdirs();
        EMOJI_DIRECTORY.mkdirs();
        PICTURE_DIRECTORY.mkdirs();
    }

    private PluginManager pluginManager = new PluginManager(PLUGIN_DIRECTORY);

    private ConsoleCommandSender consoleCommandSender = new ConsoleCommandSender();

    private PrivateInteractorManager privateInteractorManager = new PrivateInteractorManager();

    private GroupInteractorManager groupInteractorManager = new GroupInteractorManager();

    private CommandManager commandManager = new CommandManager();

    private PermissionSystem permissionSystem = PermissionSystem.forFile(new File(CONFIG_DIRECTORY, "permissions.json"));

    private EmojiManager emojiManager = new EmojiManager(EMOJI_DIRECTORY);

    private GroupManager groupManager = GroupManager.forFileOrNew(new File(CONFIG_DIRECTORY, "groups.json"), GroupManager.class, () -> {
        GroupManager manager = new GroupManager();
        manager.setGroups(new HashMap<>());
        return manager;
    });

    private PictureManager pictureManager = PictureManager.forFile(PICTURE_DIRECTORY);

    private PluginConfig pluginConfig = PluginConfig.forFile(new File(CONFIG_DIRECTORY, "plugins.json"));

    private MessageManager messageManager = new MessageManager(MESSAGE_DIRECTORY);

    private XiaomingConfig xiaomingConfig = XiaomingConfig.forFileOrNew(new File(CONFIG_DIRECTORY, "config.json"),
            XiaomingConfig.class,
            () -> {
                XiaomingConfig config = new XiaomingConfig();
                config.setCallCouter(0);
                return config;
            });

    private UserManager userManager = UserManager.forFileOrNew(new File(CONFIG_DIRECTORY, "users.json"),
            UserManager.class,
            () -> {
                UserManager manager = new UserManager();
                manager.setUsers(new HashMap<>());
                return manager;
            });

    private TimeTaskManager timeTaskManager = TimeTaskManager.forFileOrNew(new File(CONFIG_DIRECTORY, "timetasks.json"), TimeTaskManager.class, () -> {
        TimeTaskManager manager = new TimeTaskManager();
        manager.setTimeTasks(new ArrayList<>());
        return manager;
    });


    public void reloadGroupManager() {
        groupManager = GroupManager.forFileOrNew(new File(CONFIG_DIRECTORY, "groups.json"), GroupManager.class, () -> {
            GroupManager manager = new GroupManager();
            manager.setGroups(new HashMap<>());
            return manager;
        });
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
    public PictureManager getPictureManager() {
        return pictureManager;
    }

    @Override
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public void post(@NotNull SimbotContext context) {
        XiaomingBot instance = getInstance();
        registerCoreCommandExecutor();
        try {
            instance.getPluginManager().loadAllPlugins(instance.consoleCommandSender);
        }
        catch (Exception exception) {
            logger.error("初始化插件时出现异常：{}", exception);
            exception.printStackTrace();
        }

        Bot defaultBot = context.getBotManager().getDefaultBot();
        Sender sender = defaultBot.getSender().SENDER;

        for (Group test : groupManager.getGroups("test")) {
            sender.sendGroupMsg(test.getCode(), "小明已启动 (๑•̀ㅂ•́)و✧");
        }
    }

    @Override
    public void pre(@NotNull Configuration config) {}

    public CommandSender getConsoleCommandSender() {
        return consoleCommandSender;
    }

    public void registerCoreCommandExecutor() {
        commandManager.register(new CoreCommandExecutor(), null);
        commandManager.register(new PermissionCommandExecutor(), null);
        commandManager.register(new UserEventCommandExecutor(), null);
    }

    public static void main(String[] args) {
        SimbotApp.run(XiaomingBot.class, args);
        SERVICE.execute(new ConsoleCommandListener());
    }

    public EmojiManager getEmojiManager() {
        return emojiManager;
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public void execute(Thread thread) {
        SERVICE.execute(thread);
    }

    @Override
    public void execute(Runnable runnable) {
        SERVICE.execute(runnable);
    }

    @Override
    public XiaomingConfig getXiaomingConfig() {
        return xiaomingConfig;
    }

    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public TimeTaskManager getTimeTaskManager() {
        return timeTaskManager;
    }
}
