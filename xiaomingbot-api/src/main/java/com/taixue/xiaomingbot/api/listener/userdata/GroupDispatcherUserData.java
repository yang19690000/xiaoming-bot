package com.taixue.xiaomingbot.api.listener.userdata;

import catcode.CatCodeUtil;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import love.forte.simbot.api.sender.MsgSender;

/**
 * 群内交互器的数据类型
 */
public class GroupDispatcherUserData extends DispatcherUserData {
    protected long group;
    protected GroupInteractor interactor;

    public GroupInteractor getInteractor() {
        return interactor;
    }

    public void setInteractor(GroupInteractor interactor) {
        this.interactor = interactor;
    }

    public void setGroup(long group) {
        this.group = group;
    }

    public long getGroup() {
        return group;
    }

    public String at(long qq) {
        return CatCodeUtil.getInstance().getStringTemplate().at(qq);
    }

    public void atTell(long group, long qq, String message, MsgSender msgSender) {
        msgSender.SENDER.sendGroupMsg(group, at(qq) + message);
    }
}
