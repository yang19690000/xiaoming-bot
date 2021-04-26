package com.taixue.xiaoming.bot.api.user;

import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author Chuanwise
 */
public abstract class QQXiaomingUserImpl extends XiaomingUserImpl implements QQXiaomingUser {
    private AccountInfo accountInfo;
    private MsgSender msgSender;

    @Override
    public boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }

    @Override
    public String getName() {
        return getQQString();
    }

    @Override
    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    @Override
    public MsgSender getMsgSender() {
        return msgSender;
    }

    public void setMsgSender(MsgSender msgSender) {
        this.msgSender = msgSender;
    }
}
