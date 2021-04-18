package com.taixue.xiaomingbot.host.listener.dispatcher;

import catcode.CatCodeUtil;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUser;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.util.DateUtil;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

import java.util.Map;
import java.util.Objects;

/**
 * 组内信息调度器
 * @param <UserData>
 */
public abstract class GroupDispatcher<UserData extends GroupDispatcherUser>
        extends Dispatcher<UserData> {
    public String at(UserData userData) {
        return userData.at();
    }

    public String at(long qq) {
        return CatCodeUtil.getInstance().getStringTemplate().at(qq);
    }

    public abstract GroupInteractor getInteractor(UserData userData);

    @Override
    public void onThrowable(Throwable throwable, UserData userData) {
        throwable.printStackTrace();

        StringBuilder builder = new StringBuilder("【出现异常】");
        builder.append("\n").append("触发人：" + userData.getQQ());
        builder.append("\n").append("时间：" + DateUtil.format.format(System.currentTimeMillis()));
        builder.append("\n").append("异常信息：").append(throwable);

        String result = builder.toString();
        System.err.println(result);
        userData.atSendGroupMessage("呜呜呜我遇到了一些问题，错误报告已经提交了");
    }

    public void onGroupMessage(GroupMsg groupMsg, MsgSender msgSender) {
        long qq = groupMsg.getAccountInfo().getAccountCodeNumber();
        UserData userData = userDataIsolator.getUserData(qq);
        userData.setGroupMsg(groupMsg);
        userData.setMsgSender(msgSender);
        try {
            dispatch(userData);
        }
        catch (Throwable throwable) {
            onThrowable(throwable, userData);
        }
    }

    @Override
    public void dispatch(UserData userData) throws Exception {
        GroupInteractor processor = userData.getInteractor();
        long group = userData.getGroup();
        long qq = userData.getQQ();

        if (parseCommand(userData)) {
            XiaomingBot.getInstance().getXiaomingConfig().increaseCallCounter();
            return;
        }

        // 给各插件交互
        Map<String, XiaomingPlugin> loadedPlugins = XiaomingBot.getInstance().getPluginManager().getLoadedPlugins();
        for (XiaomingPlugin value : loadedPlugins.values()) {
            if (!XiaomingBot.getInstance().getPluginConfig().unableInGroup(value.getName(), group) &&
                    value.onGroupMessage(userData)) {
                XiaomingBot.getInstance().getXiaomingConfig().increaseCallCounter();
                return;
            }
        }

        if (Objects.isNull(processor) || processor.isFinished(qq)) {
            GroupInteractor newInteractor = getInteractor(userData);
            if (Objects.nonNull(newInteractor)) {
                if (Objects.nonNull(processor) && processor.isFinished(qq)) {
                    userData.getInteractor().onUserOut(qq);
                }
                userData.setInteractor(newInteractor);
                newInteractor.onUserIn(userData);
            }
            else {
                onNullProcessor(userData);
            }
        }
        else {
//            if ("退出".equals(message)) {
//                userData.getInteractor().exit(qq, group, msgSender);
//                userData.setInteractor(null);
//                msgSender.SENDER.sendGroupMsg(group, at +"已退出当前模式");
//            }
//            else {
                processor.interact(userData);
//            }
        }
    }
}
