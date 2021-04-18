package com.taixue.xiaomingbot.api.command;

import love.forte.simbot.api.message.containers.AccountCodeContainer;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

public abstract class PrivateCommandSender extends CommandSender {
    private final AccountInfo accountInfo;

    public PrivateCommandSender(PrivateMsg privateMsg) {
        this(privateMsg.getAccountInfo());
    }

    public PrivateCommandSender(AccountInfo accountInfo) {
        super(accountInfo.getAccountRemarkOrNickname() + "（" + accountInfo.getAccountCodeNumber() + "）");
        this.accountInfo = accountInfo;
    }

    public long getAccountCode() {
        return accountInfo.getAccountCodeNumber();
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public String getAccountCodeString() {
        return accountInfo.getAccountCode();
    }

    public abstract MsgSender getMsgSender();
}
