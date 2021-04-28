package com.taixue.xiaoming.bot.api.listener.interactor.user;

import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import love.forte.simbot.api.sender.MsgSender;

public interface InteractorUser extends QQXiaomingUser {
    MessageWaiter getMessageWaiter();

    void setMessageWaiter(MessageWaiter messageWaiter);

    boolean isExit();

    void shouldExit();

    String getMessage();

    boolean isFirstInteract();

    void setFirstInteract(boolean firstInteract);

    void setMsgSender(MsgSender msgSender);
}
