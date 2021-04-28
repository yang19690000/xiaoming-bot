package com.taixue.xiaoming.bot.api.user;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.base.XiaomingObject;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import org.jetbrains.annotations.NotNull;

public interface XiaomingUser extends XiaomingObject {
    void sendMessage(String message,
                     Object... arguments);

    void sendError(String message,
                   Object... arguments);

    void sendWarning(String message,
                     Object... arguments);

    boolean hasPermission(String node);

    boolean hasPermissions(@NotNull String[] nodes);

    String getName();
}
