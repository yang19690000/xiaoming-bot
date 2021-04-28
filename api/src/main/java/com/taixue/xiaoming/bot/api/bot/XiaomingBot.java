package com.taixue.xiaoming.bot.api.bot;

import com.taixue.xiaoming.bot.api.account.AccountManager;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.api.emoji.EmojiManager;
import com.taixue.xiaoming.bot.api.factory.normal.FileSavedDataFactory;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.api.limit.UserCallLimitManager;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorManager;
import com.taixue.xiaoming.bot.api.permission.PermissionManager;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.api.url.UrlInCatCodeManager;
import org.jetbrains.annotations.NotNull;

public interface XiaomingBot {
    @NotNull
    static XiaomingBot getInstance() {
        return XiaomingBotContainer.getBot();
    }

    FileSavedDataFactory getFileSavedDataFactory();

    Config getConfig();

    EmojiManager getEmojiManager();

    InteractorManager getInteractorManager();

    PermissionManager getPermissionManager();

    void execute(Runnable runnable);

    GroupManager getGroupManager();

    CommandManager getCommandManager();

    PluginManager getPluginManager();

    AccountManager getAccountManager();

    UrlInCatCodeManager getPictureManager();

    UserCallLimitManager getUserCallLimitManager();
}
