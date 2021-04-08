package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.listener.base.UserDataIsolatedChooser;
import com.taixue.xiaomingbot.api.listener.userdata.DispatcherUserData;
import com.taixue.xiaomingbot.api.listener.userdata.InteractorUserData;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaomingbot.util.ArgsUtil;
import love.forte.simbot.api.sender.MsgSender;

import java.util.List;

/**
 * 所有交互器的超类
 * @param <UserData>
 */
public abstract class Interactor
        <UserData extends InteractorUserData, DispatcherMessage extends DispatcherUserData>
        extends UserDataIsolatedChooser<UserData> {

    protected XiaomingPlugin plugin;

    public XiaomingPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(XiaomingPlugin plugin) {
        this.plugin = plugin;
    }

    public void setFinished(long qq) {
        setFinished(userDataIsolator.getUserData(qq));
    }

    public void setFinished(UserData userData) {
        userData.setShouldExit(true);
    }

    public boolean isFinished(long qq) {
        return userDataIsolator.getUserData(qq).isShouldExit();
    }

    public boolean parseCommand(UserData userData, MsgSender msgSender) {
        long qq = userData.getQQ();
        String message = userData.getMessage();

        if (message.startsWith("!") || message.startsWith("！")) {
            message = message.substring(1);
            List<String> args = ArgsUtil.splitArgs(message);
            if (args.isEmpty()) {
                return false;
            }
            if (args.get(0).equals("调试")) {
                if (args.size() != 3) {
                    return false;
                }
                switch (args.get(1)) {
                    case "迁移":
                        userData.toState(args.get(2));
                        msgSender.SENDER.sendPrivateMsg(qq, "已强制迁移状态：" +
                                userData.getLastState() + " => " + userData.currentState());
                        return true;
                    case "状态":
                        msgSender.SENDER.sendPrivateMsg(qq, "状态链：" +userData.getStatesChain());
                        return true;
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    public abstract void interact(DispatcherMessage dispatcherMessage, MsgSender msgSender);

    public abstract void showThrowable(Throwable throwable, UserData userData, MsgSender msgSender);

    public void onDefault(UserData userData, MsgSender msgSender) {}
}
