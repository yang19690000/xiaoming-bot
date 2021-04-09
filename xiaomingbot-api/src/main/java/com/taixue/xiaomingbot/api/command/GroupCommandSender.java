package com.taixue.xiaomingbot.api.command;

import love.forte.simbot.api.sender.MsgSender;

public abstract class GroupCommandSender extends CommandSender {
    protected final long group, qq;

    public GroupCommandSender(long group, long qq) {
        super(qq + "");
        this.group = group;
        this.qq = qq;
    }

    public long getGroup() {
        return group;
    }

    public long getQq() {
        return qq;
    }

    public abstract MsgSender getMsgSender();
}
