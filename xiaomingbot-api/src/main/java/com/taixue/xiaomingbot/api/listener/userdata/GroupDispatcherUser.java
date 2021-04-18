package com.taixue.xiaomingbot.api.listener.userdata;

import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.util.ArgumentUtil;
import com.taixue.xiaomingbot.util.AtUtil;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

import java.util.NoSuchElementException;

/**
 * 群内交互器的数据类型
 */
public class GroupDispatcherUser extends DispatcherUser implements GroupUserData {
    private GroupInteractor interactor;
    private GroupMsg groupMsg;
    private MsgSender msgSender;

    public void setMsgSender(MsgSender msgSender) {
        this.msgSender = msgSender;
    }

    public MsgSender getMsgSender() {
        return msgSender;
    }

    public String getMessage() {
        return groupMsg.getMsg();
    }

    @Override
    public GroupMsg getGroupMsg() {
        return groupMsg;
    }

    public void setGroupMsg(GroupMsg groupMsg) {
        this.groupMsg = groupMsg;
    }

    public GroupInteractor getInteractor() {
        return interactor;
    }

    public void setInteractor(GroupInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void sendGroupMessage(String message, Object... arguments) {
        try {
            message = ArgumentUtil.replaceArguments(message, arguments);
            msgSender.SENDER.sendGroupMsg(getGroup(), message);
        }
        catch (NoSuchElementException e) {
            StringBuilder builder = new StringBuilder(message);
            int left = builder.indexOf("[CAT:at,code="), right = 0;
            while (left != -1) {
                right = builder.indexOf("]", left);

                if (left < right) {
                    String body = builder.substring(left, right + 1);
                    if (body.matches("\\[CAT:at,code=\\d+\\]")) {
                        String qqString = AtUtil.parseQQ(body) + "";
                        builder.replace(left, right + 1, qqString);
                        left += qqString.length();
                    }
                }
            }
            msgSender.SENDER.sendGroupMsg(getGroup(), builder.toString() +
                    "\n（消息当中 @ 本群不存在的成员，无法原样发送）");
        }
    }

    @Override
    public void sendPrivateMessage(String message, Object... arguments) {
        msgSender.SENDER.sendPrivateMsg(getAccountInfo(), ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        atSendGroupMessage(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public AccountInfo getAccountInfo() {
        return getGroupMsg().getAccountInfo();
    }
}
