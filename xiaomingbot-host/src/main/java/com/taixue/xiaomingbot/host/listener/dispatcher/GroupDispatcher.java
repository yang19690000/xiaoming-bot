package com.taixue.xiaomingbot.host.listener.dispatcher;

import catcode.CatCodeUtil;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUserData;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

import java.util.Objects;

/**
 * 组内信息调度器
 * @param <UserData>
 */
public abstract class GroupDispatcher<UserData extends GroupDispatcherUserData>
        extends Dispatcher<UserData> {
    public void atTell(long group, long qq, String message, MsgSender msgSender) {
        msgSender.SENDER.sendGroupMsg(group, at(qq) + message);
    }

    public void atTell(UserData userData, String message, MsgSender msgSender) {
        atTell(userData.getGroup(), userData.getQQ(), message, msgSender);
    }

    public String at(long qq) {
        return CatCodeUtil.getInstance().getStringTemplate().at(qq);
    }

    public abstract GroupInteractor getInteractor(UserData userData);

    @Override
    public void showThrowable(Throwable throwable, UserData userData, MsgSender sender) {
        super.showThrowable(throwable, userData, sender);
        atTell(userData, "呜呜呜我遇到了一些问题，错误报告已经提交了", sender);
    }

    public void dispatch(GroupMsg groupMsg, MsgSender msgSender) {
        long qq = groupMsg.getAccountInfo().getAccountCodeNumber();
        UserData userData = userDataIsolator.getUserData(qq);
        userData.setMessage(groupMsg.getMsg());
        userData.setGroup(groupMsg.getGroupInfo().getGroupCodeNumber());
        try {
            dispatch(userData, msgSender);
        }
        catch (Throwable throwable) {
            showThrowable(throwable, userData, msgSender);
        }
    }

    public void dispatch(UserData userData, MsgSender msgSender) throws Exception {
        GroupInteractor processor = userData.getInteractor();
        long group = userData.getGroup();
        long qq = userData.getQQ();
        String at = CatCodeUtil.getInstance().getStringTemplate().at(qq);
        String message = userData.getMessage();

        if (parseCommand(userData, msgSender)) {
            return;
        }

        if (Objects.isNull(processor) || processor.isFinished(qq)) {
            GroupInteractor newInteractor = getInteractor(userData);
            if (Objects.nonNull(newInteractor)) {
                if (Objects.nonNull(processor) && processor.isFinished(qq)) {
                    userData.getInteractor().exit(qq, group, msgSender);
                }
                userData.setInteractor(newInteractor);
                newInteractor.init(qq, group, msgSender);
            }
            else {
                onNullProcessor(userData, msgSender);
            }
        }
        else {
//            if ("退出".equals(message)) {
//                userData.getInteractor().exit(qq, group, msgSender);
//                userData.setInteractor(null);
//                msgSender.SENDER.sendGroupMsg(group, at +"已退出当前模式");
//            }
//            else {
                processor.interact(userData, msgSender);
//            }
        }
    }
}
