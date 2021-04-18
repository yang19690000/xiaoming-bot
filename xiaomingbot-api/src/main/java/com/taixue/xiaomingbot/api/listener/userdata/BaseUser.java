package com.taixue.xiaomingbot.api.listener.userdata;

import com.taixue.xiaomingbot.api.user.UserEvent;
import love.forte.simbot.api.message.containers.AccountInfo;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 状态型调度器或分派器的用户数据基础内容
 */
public abstract class BaseUser {
    private MessageWaiter messageWaiter;

    public MessageWaiter getMessageWaiter() {
        return messageWaiter;
    }

    public void setMessageWaiter(MessageWaiter messageWaiter) {
        this.messageWaiter = messageWaiter;
    }

    public abstract void sendMessage(String message, Object... arguments);

    public abstract AccountInfo getAccountInfo();
}
