package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUserData;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateInteractorUserData;
import love.forte.simbot.api.sender.MsgSender;

import java.lang.reflect.Method;

/**
 * 可供调度的私聊消息交互器
 * @param <UserData>
 */
public abstract class PrivateInteractor<UserData extends PrivateInteractorUserData>
        extends Interactor<UserData, PrivateDispatcherUserData> {

    public void tell(long qq, String message, MsgSender msgSender) {
        msgSender.SENDER.sendPrivateMsg(qq, message);
    }

    public void tell(UserData userData, String message, MsgSender msgSender) {
        tell(userData.getQQ(), message, msgSender);
    }

    public abstract boolean isInteractor(PrivateDispatcherUserData userData);

    public void init(long qq, MsgSender sender) {
        userDataIsolator.registerUserData(qq);
    }

    public void exit(long qq, MsgSender sender) {
        userDataIsolator.removeUserData(qq);
    }

    @Override
    public void showThrowable(Throwable throwable, UserData userData, MsgSender msgSender) {
        StringBuilder builder = new StringBuilder("我是傻逼 (；′⌒`)\n");
//        DebugUtil.showThrowable(throwable, userData, msgSender);
        String lastState = userData.getLastState();
        String curState = userData.currentState();
        if (userData.toLastState()) {
            builder.append("错误报告已反馈，正在回滚上一步状态：" + curState + " => " + lastState);
        }
        else {
            builder.append("无法自动修复，已尝试迁移至默认状态，错误报告已反馈。");
            userData.toDefaultState();
        }
        msgSender.SENDER.sendPrivateMsg(userData.getQQ(), builder.toString());
    }

    public void interact(PrivateDispatcherUserData dispatcherMessage, MsgSender msgSender) {
        UserData userData = userDataIsolator.getUserData(dispatcherMessage.getQQ());
        userData.setMessage(dispatcherMessage.getMessage());

        if (parseCommand(userData, msgSender)) {
            return;
        }
        Class modeClass = userData.getClass();

        try {
            Method stateMethod = this.getClass().getDeclaredMethod("on" + userData.currentState(),
                    modeClass, MsgSender.class);
            stateMethod.invoke(this, userData, msgSender);
        }
        catch (Throwable throwable) {
            showThrowable(throwable, userData, msgSender);
        }
    }
}