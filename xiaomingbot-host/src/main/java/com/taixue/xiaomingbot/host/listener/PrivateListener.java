package com.taixue.xiaomingbot.host.listener;

import com.taixue.xiaomingbot.host.listener.dispatcher.PrivateDispatcher;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUserData;
import com.taixue.xiaomingbot.host.XiaomingBot;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class PrivateListener extends PrivateDispatcher<PrivateDispatcherUserData> {
    @Override
    public boolean parseCommand(PrivateDispatcherUserData userData, MsgSender msgSender) {
        return false;
    }

    @Override
    public PrivateDispatcherUserData newUserData() {
        return new PrivateDispatcherUserData();
    }

    @Override
    public void onNullProcessor(PrivateDispatcherUserData userData, MsgSender msgSender) {
        tell(userData, "小明不知道这是什么意思qwq！", msgSender);
    }

    @Override
    public PrivateInteractor getInteractor(PrivateDispatcherUserData userData) {
        return XiaomingBot.getInstance().getPrivateInteractorManager().getInteractor(userData);
    }

    @OnPrivate
    public void onPrivate(PrivateMsg privateMsg, MsgSender msgSender) {
        dispatch(privateMsg, msgSender);
    }
}
