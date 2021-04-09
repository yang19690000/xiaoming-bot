package com.taixue.xiaomingbot.host.listener;

import com.taixue.xiaomingbot.host.command.sender.GroupCommandSender;
import com.taixue.xiaomingbot.host.listener.dispatcher.GroupDispatcher;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUserData;
import com.taixue.xiaomingbot.host.XiaomingBot;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author Chuanwise
 */
@Beans
public class GroupListener extends GroupDispatcher<GroupDispatcherUserData> {
    @Override
    public boolean parseCommand(GroupDispatcherUserData userData, MsgSender msgSender) {
        return XiaomingBot.getInstance().getCommandManager().onCommand(
                new GroupCommandSender(msgSender, userData.getGroup(), userData.getQQ()), userData.getMessage());
    }

    @Override
    public void onNullProcessor(GroupDispatcherUserData userData, MsgSender msgSender) {}

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
        long group = groupMsg.getGroupInfo().getGroupCodeNumber();
        if (XiaomingBot.getInstance().getGroupManager().isGroup("test", group)) {
            dispatch(groupMsg, msgSender);
        }
    }
}
