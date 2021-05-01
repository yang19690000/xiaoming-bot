package com.taixue.xiaoming.bot.api.bot;

import com.taixue.xiaoming.bot.api.account.AccountManager;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.config.BotAccountConfig;
import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.api.config.Counter;
import com.taixue.xiaoming.bot.api.data.RegularSaveDataManager;
import com.taixue.xiaoming.bot.api.emoji.EmojiManager;
import com.taixue.xiaoming.bot.api.error.ErrorMessageManager;
import com.taixue.xiaoming.bot.api.factory.normal.FileSavedDataFactory;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.api.limit.UserCallLimitManager;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorManager;
import com.taixue.xiaoming.bot.api.permission.PermissionManager;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.api.url.UrlInCatCodeManager;
import com.taixue.xiaoming.bot.api.user.ConsoleXiaomingUser;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

public interface XiaomingBot {
    @NotNull
    static XiaomingBot getInstance() {
        return XiaomingBotContainer.getBot();
    }

    @NotNull
    MsgSender getMsgSender();

    FileSavedDataFactory getFileSavedDataFactory();

    BotAccountConfig getBotAccountConfig();

    Config getConfig();

    EmojiManager getEmojiManager();

    InteractorManager getInteractorManager();

    PermissionManager getPermissionManager();

    void execute(Runnable runnable);

    ExecutorService getService();

    GroupManager getGroupManager();

    CommandManager getCommandManager();

    PluginManager getPluginManager();

    AccountManager getAccountManager();

    UrlInCatCodeManager getPictureManager();

    UserCallLimitManager getUserCallLimitManager();

    Counter getCounter();

    ErrorMessageManager getErrorMessageManager();

    RegularSaveDataManager getRegularSaveDataManager();

    ConsoleDispatcherUser getConsoleXiaomingUser();
}
