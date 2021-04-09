package com.taixue.xiaominglexicons.listener;

import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUserData;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateInteractorUserData;
import love.forte.simbot.api.sender.MsgSender;

public class PrivateMessageRepeater extends PrivateInteractor<PrivateInteractorUserData> {
    @Override
    public boolean isInteractor(PrivateDispatcherUserData userData) {
        return true;
    }

    @Override
    public void onDefault(PrivateInteractorUserData userData, MsgSender msgSender) {
        tell(userData, userData.getMessage(), msgSender);
    }

    @Override
    public PrivateInteractorUserData newUserData() {
        return new PrivateInteractorUserData();
    }
}
