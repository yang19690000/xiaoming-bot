package com.taixue.xiaomingbot.host.listener.dispatcher;

import com.taixue.xiaomingbot.api.listener.interactor.PrivateInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.PrivateDispatcherUserData;
import com.taixue.xiaomingbot.util.ArgsUtil;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

import java.util.List;
import java.util.Objects;

/**
 * 私聊信息的调度器
 */
public abstract class PrivateDispatcher<UserData extends PrivateDispatcherUserData>
        extends Dispatcher<UserData> {
    public void tell(long qq, String message, MsgSender msgSender) {
        msgSender.SENDER.sendPrivateMsg(qq, message);
    }

    public void tell(UserData userData, String message, MsgSender msgSender) {
        tell(userData.getQQ(), message, msgSender);
    }

    public void dispatch(UserData userData, MsgSender msgSender) throws Exception {
        PrivateInteractor processor = userData.getInteractor();
        long qq = userData.getQQ();
        String message = userData.getMessage();

        if (parseCommand(userData, msgSender)) {
            return;
        }

        if (Objects.isNull(processor) || processor.isFinished(qq)) {
            PrivateInteractor newProcess = getInteractor(userData);
            if (Objects.nonNull(newProcess)) {
                if (Objects.nonNull(processor) && processor.isFinished(qq)) {
                    userData.getInteractor().exit(qq, msgSender);
                }
                userData.setInteractor(newProcess);
                newProcess.init(qq, msgSender);
            }
            else {
                onNullProcessor(userData, msgSender);
            }
        }
        else {
//            if (message.equals("退出")) {
//                userData.getInteractor().exit(qq, msgSender);
//                userData.setInteractor(null);
//                msgSender.SENDER.sendPrivateMsg(qq, "已退出当前模式");
//            }
//            else {
                processor.interact(userData, msgSender);
//            }
        }
    }

    public void dispatch(PrivateMsg privateMsg, MsgSender msgSender) {
        long qq = privateMsg.getAccountInfo().getAccountCodeNumber();
        UserData userData = userDataIsolator.getUserData(qq);
        userData.setMessage(privateMsg.getMsg());
        try {
            dispatch(userData, msgSender);
        }
        catch (Throwable throwable) {
            showThrowable(throwable, userData, msgSender);
        }
    }

    public abstract PrivateInteractor getInteractor(PrivateDispatcherUserData userData);

    public boolean parseCommand(PrivateMsg privateMsg, MsgSender msgSender, String message, UserData userData) {
        long qq = privateMsg.getAccountInfo().getAccountCodeNumber();
        if (message.startsWith("！") || message.startsWith("!")) {
            message = message.substring(1);
            List<String> args = ArgsUtil.splitArgs(message);
            if (args.isEmpty()) {
                return false;
            }
            if (args.get(0).equals("调度")) {
                if (args.size() == 2) {
                    PrivateInteractor newProcessor = getInteractor(userData);
                    if (Objects.nonNull(newProcessor)) {
                        if (Objects.nonNull(userData.getInteractor())) {
                            userData.getInteractor().exit(qq, msgSender);
                        }
                        userData.setInteractor(newProcessor);
                        msgSender.SENDER.sendPrivateMsg(privateMsg, "调度成功");
                        userData.getInteractor().init(qq, msgSender);
                        if (userData.getInteractor().isFinished(qq)) {
                            userData.getInteractor().exit(qq, msgSender);
                            userData.setInteractor(null);
                        }
                    }
                    else {
                        msgSender.SENDER.sendPrivateMsg(privateMsg, "调度失败，原因：未找到分派器「" + args.get(1) + "」");
                    }
                }
                else {
                    msgSender.SENDER.sendPrivateMsg(privateMsg, "调度失败，原因：分派器不能为空");
                }
                return true;
            }
        }
        return false;
    }
}