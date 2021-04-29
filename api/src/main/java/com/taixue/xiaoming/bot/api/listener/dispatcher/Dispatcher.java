package com.taixue.xiaoming.bot.api.listener.dispatcher;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.api.base.XiaomingObject;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.NotNull;

public interface Dispatcher<UserDataType extends DispatcherUser> extends HostObject {
    boolean onMessage(UserDataType user);

    void reportExceptionToLog(@NotNull DispatcherUser user,
                              @NotNull Exception exception,
                              @NotNull XiaomingPlugin plugin,
                              @NotNull MsgSender msgSender);

    void reportExceptionToLog(@NotNull DispatcherUser user,
                              @NotNull Exception exception,
                              @NotNull Interactor interactor,
                              @NotNull MsgSender msgSender);

    void reportExceptionToLog(@NotNull DispatcherUser user,
                              @NotNull Exception exception,
                              @NotNull CommandExecutor executor,
                              @NotNull MsgSender msgSender);

    void onInteractorNotFound(@NotNull UserDataType user);
}
