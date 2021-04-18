package com.taixue.xiaomingbot.api.command;

import com.taixue.xiaomingbot.util.ArgumentUtil;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

public abstract class GroupCommandSender extends PrivateCommandSender {
    private final GroupInfo groupInfo;

    public GroupCommandSender(GroupInfo groupInfo, AccountInfo accountInfo) {
        super(accountInfo);
        this.groupInfo = groupInfo;
    }

    public GroupCommandSender(GroupMsg groupMsg) {
        this(groupMsg.getGroupInfo(), groupMsg.getAccountInfo());
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public long getGroupCode() {
        return groupInfo.getGroupCodeNumber();
    }

    public String getGroupCodeString() {
        return groupInfo.getGroupCode();
    }

    @Override
    public abstract MsgSender getMsgSender();

    @Override
    public void sendMessage(String message, Object... arguments) {
        getMsgSender().SENDER.sendGroupMsg(groupInfo, ArgumentUtil.replaceArguments(message, arguments));
    }
}
