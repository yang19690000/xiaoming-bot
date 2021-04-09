package com.taixue.xiaomingbot.api.command;

public abstract class GroupCommandSender extends CommandSender {
    protected final long group, qq;

    public GroupCommandSender(long group, long qq) {
        super(qq + "");
        this.group = group;
        this.qq = qq;
    }

    @Override
    public boolean hasPermission(String node) {
        return false;
    }
}
