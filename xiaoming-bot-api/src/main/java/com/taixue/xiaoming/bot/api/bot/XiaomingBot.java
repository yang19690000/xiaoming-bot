package com.taixue.xiaoming.bot.api.bot;

import com.taixue.xiaoming.bot.api.account.AccountManager;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.api.emoji.EmojiManager;
import com.taixue.xiaoming.bot.api.factory.normal.JsonFileSavedDataFactory;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.api.limit.CallLimitManager;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorManager;
import com.taixue.xiaoming.bot.api.permission.PermissionGroup;
import com.taixue.xiaoming.bot.api.permission.PermissionManager;
import com.taixue.xiaoming.bot.api.picture.PictureManager;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XiaomingBot {
    // 和小明本体相关的方法
    private static final XiaomingBot INSTANCE = new XiaomingBot();

    @NotNull
    public static XiaomingBot getInstance() {
        return INSTANCE;
    }

    private final JsonFileSavedDataFactory jsonFileSavedDataFactory = new JsonFileSavedDataFactory();

    public JsonFileSavedDataFactory getJsonFileSavedDataFactory() {
        return jsonFileSavedDataFactory;
    }

    // 小明各大组件
    private Config config = jsonFileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "config.json"),
                Config.class,
                    Config::new);

    public Config getConfig() {
        return config;
    }

    private EmojiManager emojiManager = jsonFileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "emoji.json"),
                    EmojiManager.class,
                    EmojiManager::new);

    public EmojiManager getEmojiManager() {
        return emojiManager;
    }

    private InteractorManager interactorManager = new InteractorManager();

    public InteractorManager getInteractorManager() {
        return interactorManager;
    }

    private PermissionManager permissionManager = jsonFileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "permission.json"),
                    PermissionManager.class,
                    () -> {
                        PermissionManager manager = new PermissionManager();
                        final PermissionGroup defaultGroup = new PermissionGroup();
                        defaultGroup.setAlias("默认组");
                        manager.addGroup(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME,
                                defaultGroup);
                        return manager;
                    });

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    private GroupManager groupManager = jsonFileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "group.json"),
                    GroupManager.class,
                    GroupManager::new);

    public static final int MAX_THREAD_NUMBER = 20;

    public static final ExecutorService SERVICE = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);

    public void execute(final Runnable runnable) {
        SERVICE.execute(runnable);
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    private CommandManager commandManager = new CommandManager();

    public CommandManager getCommandManager() {
        return commandManager;
    }

    private PluginManager pluginManager = new PluginManager(PathUtil.PLUGIN_DIR);

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    private AccountManager accountManager = new AccountManager(PathUtil.ACCOUNT_DIR);

    public AccountManager getAccountManager() {
        return accountManager;
    }

    private PictureManager pictureManager = jsonFileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "picture.json"),
                    PictureManager.class,
                    PictureManager::new);

    public PictureManager getPictureManager() {
        return pictureManager;
    }

    private CallLimitManager callLimitManager = jsonFileSavedDataFactory.forFileOrProduce(new File(PathUtil.CONFIG_DIR,
            "limit.json"),
            CallLimitManager.class,
            CallLimitManager::new);

    public CallLimitManager getCallLimitManager() {
        return callLimitManager;
    }
}
