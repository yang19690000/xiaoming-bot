package com.taixue.xiaomingbot.api.listener.userdata;
/**
 * 调度器用户数据
 * @author Chuanwise
 */
public abstract class DispatcherUser extends BaseUser {
    private MessageWaiter messageWaiter;

    @Override
    public MessageWaiter getMessageWaiter() {
        return messageWaiter;
    }

    @Override
    public void setMessageWaiter(MessageWaiter messageWaiter) {
        this.messageWaiter = messageWaiter;
    }
}
