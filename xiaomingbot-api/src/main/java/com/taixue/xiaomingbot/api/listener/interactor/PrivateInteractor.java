package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.exception.InteactorTimeoutException;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUser;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateInteractorUser;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * 可供调度的私聊消息交互器
 * @param <UserData>
 */
public abstract class PrivateInteractor<UserData extends PrivateInteractorUser>
        extends Interactor<UserData, PrivateDispatcherUser> {

    public void tell(long qq, String message, MsgSender msgSender) {
        msgSender.SENDER.sendPrivateMsg(qq, message);
    }

    public void tell(UserData userData, String message, MsgSender msgSender) {
        tell(userData.getQQ(), message, msgSender);
    }

    public void onUserIn(PrivateDispatcherUser userData) {
        onUserIn(userData.getQQ(), userData.getPrivateMsg(), userData.getMsgSender());
    }

    public void onUserIn(long qq, PrivateMsg privateMsg, MsgSender msgSender) {
        UserData userData = userDataIsolator.registerUserData(qq);
        userData.setMsgSender(msgSender);
        userData.setPrivateMsg(privateMsg);
        init(userData);
    }

    public void onUserOut(long qq) {
        exit(userDataIsolator.getUserData(qq));
        userDataIsolator.removeUserData(qq);
    }

    public void init(UserData userData) {}

    public void exit(UserData userData) {}

    @Override
    public void onGetNextInput(UserData userData) {
        userData.getMessageWaiter().onInput(userData.getMessage());
    }

    @Override
    public void onThrowable(Throwable throwable, UserData userData) {
        StringBuilder builder = new StringBuilder("我是傻逼 (；′⌒`)\n");
        userData.sendPrivateMessage(builder.toString());
        throwable.printStackTrace();
    }

    @Override
    public boolean interact(PrivateDispatcherUser dispatcherMessage) {
        UserData userData = userDataIsolator.getUserData(dispatcherMessage.getQQ());
        userData.setPrivateMsg(dispatcherMessage.getPrivateMsg());
        userData.setMsgSender(dispatcherMessage.getMsgSender());

        getPlugin().getXiaomingBot().getXiaomingConfig().increaseCallCounter();

        try {
            return interact(userData);
        }
        catch (InteactorTimeoutException timeoutException) {
            setFinished(userData);
            return true;
        }
        catch (Throwable throwable) {
            onThrowable(throwable, userData);
            return true;
        }
    }
}