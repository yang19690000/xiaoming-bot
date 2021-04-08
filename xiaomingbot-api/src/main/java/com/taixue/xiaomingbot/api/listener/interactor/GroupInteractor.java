package com.taixue.xiaomingbot.api.listener.interactor;

import catcode.CatCodeUtil;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUserData;
import com.taixue.xiaomingbot.api.listener.userdata.GroupInteractorUserData;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateInteractorUserData;
import love.forte.simbot.api.sender.MsgSender;

import java.lang.reflect.Method;

/**
 * @author Chuanwise
 */
public abstract class GroupInteractor
        <UserData extends GroupInteractorUserData>
        extends Interactor<UserData, GroupDispatcherUserData> {

    public String at(long qq) {
        return CatCodeUtil.getInstance().getStringTemplate().at(qq);
    }

    public String at(UserData userData) {
        return at(userData.getQQ());
    }

    public void say(long group, String message, MsgSender msgSender) {
        msgSender.SENDER.sendGroupMsg(group, message);
    }

    public void say(UserData userData, String message, MsgSender msgSender) {
        say(userData.getGroup(), message, msgSender);
    }

    public void atTell(UserData userData, String message, MsgSender msgSender) {
        atTell(userData.getGroup(), userData.getQQ(), message, msgSender);
    }

    public void atTell(long group, long qq, String message, MsgSender msgSender) {
        say(group, at(qq) + message, msgSender);
    }

    public abstract boolean isGroupInteractor(GroupDispatcherUserData userData);

    public void init(long qq, long group, MsgSender msgSender) {
        userDataIsolator.registerUserData(qq);
        userDataIsolator.getUserData(qq).setGroup(group);
    }

    public void exit(long qq, long group, MsgSender msgSender) {
        userDataIsolator.removeUserData(qq);
    }

    @Override
    public void interact(GroupDispatcherUserData groupDispatcherUserData, MsgSender msgSender) {
        UserData userData = userDataIsolator.getUserData(groupDispatcherUserData.getQQ());
        userData.setGroup(groupDispatcherUserData.getGroup());
        userData.setMessage(groupDispatcherUserData.getMessage());

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
        say(userData, builder.toString(), msgSender);
    }
}
