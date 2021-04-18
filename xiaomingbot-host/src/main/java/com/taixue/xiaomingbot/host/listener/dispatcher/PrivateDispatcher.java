package com.taixue.xiaomingbot.host.listener.dispatcher;

import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUser;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaomingbot.host.XiaomingBot;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

import java.util.Map;
import java.util.Objects;

/**
 * 私聊信息的调度器
 */
public abstract class PrivateDispatcher<UserData extends PrivateDispatcherUser>
        extends Dispatcher<UserData> {
    @Override
    public void onThrowable(Throwable throwable, UserData userData) {
        throwable.printStackTrace();
    }

    public void tell(long qq, String message, MsgSender msgSender) {
        msgSender.SENDER.sendPrivateMsg(qq, message);
    }

    public void tell(UserData userData, String message, MsgSender msgSender) {
        tell(userData.getQQ(), message, msgSender);
    }

    @Override
    public void dispatch(UserData userData) throws Exception {
        PrivateInteractor processor = userData.getInteractor();
        long qq = userData.getQQ();

        if (parseCommand(userData)) {
            XiaomingBot.getInstance().getXiaomingConfig().increaseCallCounter();
            return;
        }

        // 给各插件交互
        Map<String, XiaomingPlugin> loadedPlugins = XiaomingBot.getInstance().getPluginManager().getLoadedPlugins();
        for (XiaomingPlugin value : loadedPlugins.values()) {
            if (!XiaomingBot.getInstance().getPluginConfig().unableInUser(value.getName(), qq) &&
                    value.onPrivateMessage(userData)) {
                XiaomingBot.getInstance().getXiaomingConfig().increaseCallCounter();
                return;
            }
        }

        if (Objects.isNull(processor) || processor.isFinished(qq)) {
            PrivateInteractor newProcess = getInteractor(userData);
            if (Objects.nonNull(newProcess)) {
                if (Objects.nonNull(processor) && processor.isFinished(qq)) {
                    userData.getInteractor().onUserOut(qq);
                }
                userData.setInteractor(newProcess);
                newProcess.onUserIn(userData);
            }
            else {
                onNullProcessor(userData);
            }
        }
        else {
//            if (message.equals("退出")) {
//                userData.getInteractor().exit(qq, msgSender);
//                userData.setInteractor(null);
//                msgSender.SENDER.sendPrivateMsg(qq, "已退出当前模式");
//            }
//            else {
                processor.interact(userData);
//            }
        }
    }

    public void onPrivateMessage(PrivateMsg privateMsg, MsgSender msgSender) {
        long qq = privateMsg.getAccountInfo().getAccountCodeNumber();
        UserData userData = userDataIsolator.getUserData(qq);
        userData.setPrivateMsg(privateMsg);
        userData.setMsgSender(msgSender);
        try {
            dispatch(userData);
        }
        catch (Throwable throwable) {
            onThrowable(throwable, userData);
        }
    }

    public abstract PrivateInteractor getInteractor(PrivateDispatcherUser userData);
}