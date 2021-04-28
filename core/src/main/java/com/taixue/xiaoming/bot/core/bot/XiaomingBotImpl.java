package com.taixue.xiaoming.bot.core.bot;

import com.taixue.xiaoming.bot.api.account.AccountManager;
import com.taixue.xiaoming.bot.api.bot.XiaomingBotContainer;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.config.BotAccount;
import com.taixue.xiaoming.bot.api.factory.normal.FileSavedDataFactory;
import com.taixue.xiaoming.bot.api.limit.UserCallLimitManager;
import com.taixue.xiaoming.bot.core.limit.UserCallLimitManagerImpl;
import com.taixue.xiaoming.bot.core.command.executor.CommandManagerImpl;
import com.taixue.xiaoming.bot.core.config.BotAccountImpl;
import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.api.emoji.EmojiManager;
import com.taixue.xiaoming.bot.core.factory.normal.JsonFileSavedDataFactory;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.core.account.AccountManagerImpl;
import com.taixue.xiaoming.bot.core.config.ConfigImpl;
import com.taixue.xiaoming.bot.core.emoji.EmojiManagerImpl;
import com.taixue.xiaoming.bot.core.group.GroupManagerImpl;
import com.taixue.xiaoming.bot.core.limit.CallLimitConfigImpl;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorManager;
import com.taixue.xiaoming.bot.api.permission.PermissionGroup;
import com.taixue.xiaoming.bot.api.permission.PermissionManager;
import com.taixue.xiaoming.bot.api.url.UrlInCatCodeManager;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.api.limit.UserCallRecord;
import com.taixue.xiaoming.bot.core.listener.interactor.InteractorManagerImpl;
import com.taixue.xiaoming.bot.core.permission.PermissionGroupImpl;
import com.taixue.xiaoming.bot.core.permission.PermissionManagerImpl;
import com.taixue.xiaoming.bot.core.plugin.PluginManagerImpl;
import com.taixue.xiaoming.bot.core.url.UrlInCatCodeInCatCodeManagerImpl;
import com.taixue.xiaoming.bot.util.PathUtil;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XiaomingBotImpl implements XiaomingBot {
    public XiaomingBotImpl() {
        XiaomingBotContainer.setBot(this);
    }

    private final FileSavedDataFactory fileSavedDataFactory = new JsonFileSavedDataFactory();

    @Override
    public FileSavedDataFactory getFileSavedDataFactory() {
        return fileSavedDataFactory;
    }

    // 小明各大组件
    private Config config = fileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "config.json"),
                    ConfigImpl.class,
                    () -> {
                        ConfigImpl c = new ConfigImpl();
                        final BotAccount e = new BotAccountImpl(1525916855, "你的bot账户密码");
                        c.getAccounts().add(e);
                        return c;
                    });

    @Override
    public Config getConfig() {
        return config;
    }

    private EmojiManager emojiManager = fileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "emoji.json"),
                    EmojiManagerImpl.class,
                    EmojiManagerImpl::new);

    @Override
    public EmojiManager getEmojiManager() {
        return emojiManager;
    }

    private InteractorManager interactorManager = new InteractorManagerImpl();

    @Override
    public InteractorManager getInteractorManager() {
        return interactorManager;
    }

    private PermissionManager permissionManager = fileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "permission.json"),
                    PermissionManagerImpl.class,
                    () -> {
                        PermissionManagerImpl manager = new PermissionManagerImpl();
                        final PermissionGroup defaultGroup = new PermissionGroupImpl();
                        defaultGroup.setAlias("默认组");
                        manager.addGroup(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME,
                                defaultGroup);
                        return manager;
                    });

    @Override
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    private GroupManager groupManager = fileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "group.json"),
                    GroupManagerImpl.class,
                    GroupManagerImpl::new);

    public static final int MAX_THREAD_NUMBER = 20;

    public static final ExecutorService SERVICE = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);

    @Override
    public void execute(final Runnable runnable) {
        SERVICE.execute(runnable);
    }

    @Override
    public GroupManager getGroupManager() {
        return groupManager;
    }

    private CommandManager commandManager = new CommandManagerImpl();

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    private PluginManager pluginManager = new PluginManagerImpl(PathUtil.PLUGIN_DIR);

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    private AccountManager accountManager = new AccountManagerImpl(PathUtil.ACCOUNT_DIR);

    @Override
    public AccountManager getAccountManager() {
        return accountManager;
    }

    private UrlInCatCodeManager pictureManager = fileSavedDataFactory
            .forFileOrProduce(new File(PathUtil.CONFIG_DIR, "picture.json"),
                    UrlInCatCodeInCatCodeManagerImpl.class,
                    UrlInCatCodeInCatCodeManagerImpl::new);

    @Override
    public UrlInCatCodeManager getPictureManager() {
        return pictureManager;
    }

    private UserCallLimitManager userCallLimitManager = fileSavedDataFactory.forFileOrProduce(new File(PathUtil.CONFIG_DIR,
                    "limit.json"),
            UserCallLimitManagerImpl.class,
            () -> {
                UserCallLimitManagerImpl manager = new UserCallLimitManagerImpl();
                manager.getGroupCallLimiter().setConfig(new CallLimitConfigImpl());
                manager.getPrivateCallLimiter().setConfig(new CallLimitConfigImpl());
                return manager;
            });

    @Override
    public UserCallLimitManager getUserCallLimitManager() {
        return userCallLimitManager;
    }
}