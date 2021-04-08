package com.taixue.xiaomingbot.host.listener.dispatcher;

import com.taixue.xiaomingbot.api.listener.base.UserDataIsolatedChooser;
import com.taixue.xiaomingbot.api.listener.userdata.DispatcherUserData;
import com.taixue.xiaomingbot.util.DateUtil;
import love.forte.simbot.api.sender.MsgSender;

/**
 * 状态型分派器的超类
 * @param <UserData> 需要隔离的用户数据类
 */
public abstract class Dispatcher<UserData extends DispatcherUserData>
        extends UserDataIsolatedChooser<UserData> {
    public void showThrowable(Throwable throwable, UserData userData, MsgSender sender) {
        throwable.printStackTrace();

        StringBuilder builder = new StringBuilder("【出现异常】");
        builder.append("\n").append("触发人：" + userData.getQQ());
        builder.append("\n").append("时间：" + DateUtil.format.format(System.currentTimeMillis()));
        builder.append("\n").append("状态链：").append(userData.getStatesChain());
        builder.append("\n").append("异常信息：").append(throwable);

        String result = builder.toString();
        System.err.println(result);
//        GroupUtil.sendMessageToAllLogger(result, sender);
    }

    public abstract boolean parseCommand(UserData userData, MsgSender msgSender);

    public abstract void onNullProcessor(UserData userData, MsgSender msgSender);
}
