package com.taixue.xiaominglexicons.listener;

import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUserData;
import com.taixue.xiaomingbot.api.listener.userdata.GroupInteractorUserData;
import love.forte.simbot.api.sender.MsgSender;

public class GroupMessageRepeater extends GroupInteractor<GroupInteractorUserData> {
    @Override
    public boolean isGroupInteractor(GroupDispatcherUserData userData) {
        return true;
    }

    @Override
    public void onDefault(GroupInteractorUserData userData, MsgSender msgSender) {
        atTell(userData, userData.getMessage(), msgSender);
    }

    @Override
    public GroupInteractorUserData newUserData() {
        return new GroupInteractorUserData();
    }
}
