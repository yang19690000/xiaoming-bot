package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.exception.InteactorTimeoutException;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUser;
import com.taixue.xiaomingbot.api.listener.userdata.GroupInteractorUser;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author Chuanwise
 */
public abstract class GroupInteractor
        <UserData extends GroupInteractorUser>
        extends Interactor<UserData, GroupDispatcherUser> {
    public final void onUserIn(GroupDispatcherUser dispatcherUserData) {
        onUserIn(dispatcherUserData.getGroup(), dispatcherUserData.getQQ(), dispatcherUserData.getGroupMsg(), dispatcherUserData.getMsgSender());
    }

    public final void onUserIn(long group, long qq, GroupMsg groupMsg, MsgSender msgSender) {
        UserData userData = userDataIsolator.registerUserData(qq);
        userData.setMsgSender(msgSender);
        userData.setGroupMsg(groupMsg);
        init(userData);
    }

    public final void onUserOut(long qq) {
        exit(userDataIsolator.getUserData(qq));
        userDataIsolator.removeUserData(qq);
    }

    public void init(UserData userData) {}

    public void exit(UserData userData) {}

    @Override
    public final void onGetNextInput(UserData userData) {
        userData.getMessageWaiter().onInput(userData.getMessage());
    }

    @Override
    public final boolean interact(GroupDispatcherUser dispatcherUserDataGroupMsg) {
        UserData userData = userDataIsolator.getUserData(dispatcherUserDataGroupMsg.getQQ());
        userData.setGroupMsg(dispatcherUserDataGroupMsg.getGroupMsg());
        userData.setMsgSender(dispatcherUserDataGroupMsg.getMsgSender());

        try {
            getPlugin().getXiaomingBot().getXiaomingConfig().increaseCallCounter();
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

    @Override
    public void onThrowable(Throwable throwable, UserData userData) {
        StringBuilder builder = new StringBuilder("我是傻逼 (；′⌒`)\n");
        userData.atSendGroupMessage(builder.toString());
        throwable.printStackTrace();
    }

    @Override
    public UserData newUserData() {
        return (UserData) new GroupInteractorUser();
    }
}
