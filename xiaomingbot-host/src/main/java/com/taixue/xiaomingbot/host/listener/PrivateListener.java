package com.taixue.xiaomingbot.host.listener;

import com.taixue.xiaomingbot.host.command.sender.PrivateCommandSender;
import com.taixue.xiaomingbot.host.listener.dispatcher.PrivateDispatcher;
import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUser;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.util.DateUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class PrivateListener extends PrivateDispatcher<PrivateDispatcherUser> {
    @Override
    public boolean parseCommand(PrivateDispatcherUser userData) {
        return XiaomingBot.getInstance().getCommandManager().onCommand(
                new PrivateCommandSender(userData.getPrivateMsg(), userData.getMsgSender()), userData.getMessage());
    }

    @Override
    public PrivateDispatcherUser newUserData() {
        return new PrivateDispatcherUser();
    }

    @Override
    public void onNullProcessor(PrivateDispatcherUser userData) {
        userData.sendPrivateMessage("小明不知道这是什么意思qwq！");
    }

    @Override
    public PrivateInteractor getInteractor(PrivateDispatcherUser userData) {
        return XiaomingBot.getInstance().getPrivateInteractorManager().getInteractor(userData);
    }

    @OnPrivate
    public void onPrivateMessage(PrivateMsg privateMsg, MsgSender msgSender) {
        getLogger().info("[" + DateUtil.format.format(System.currentTimeMillis()) + "] " +
                privateMsg.getAccountInfo().getAccountRemarkOrNickname() +
                "（" + privateMsg.getAccountInfo().getAccountCodeNumber() +"）\t" + privateMsg.getMsg());
        super.onPrivateMessage(privateMsg, msgSender);
    }
}
