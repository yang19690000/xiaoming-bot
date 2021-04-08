package com.taixue.xiaomingbot.host.listener;

import com.taixue.xiaomingbot.host.listener.dispatcher.GroupDispatcher;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUserData;
import com.taixue.xiaomingbot.host.XiaomingBot;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class GroupListener extends GroupDispatcher<GroupDispatcherUserData> {
    @Override
    public boolean parseCommand(GroupDispatcherUserData userData, MsgSender msgSender) {
        return false;
    }

    @Override
    public void onNullProcessor(GroupDispatcherUserData userData, MsgSender msgSender) {
        atTell(userData, "小明不知道这是什么意思qwq！", msgSender);
    }

    @Override
    public GroupDispatcherUserData newUserData() {
        return new GroupDispatcherUserData();
    }

    @Override
    public GroupInteractor getInteractor(GroupDispatcherUserData userData) {
        return XiaomingBot.getInstance().getGroupInteractorManager().getInteractor(userData);
    }

    @OnGroup
    public void onGroupMessage(GroupMsg groupMsg, MsgSender msgSender) {
        if (groupMsg.getGroupInfo().getGroupCodeNumber() == 924371658) {
            dispatch(groupMsg, msgSender);
        }
    }
}
