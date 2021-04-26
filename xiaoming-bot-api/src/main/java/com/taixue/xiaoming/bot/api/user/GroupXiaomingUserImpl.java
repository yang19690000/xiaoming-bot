package com.taixue.xiaoming.bot.api.user;

import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;

import java.util.NoSuchElementException;

public class GroupXiaomingUserImpl extends QQXiaomingUserImpl {
    private GroupMsg groupMsg;

    public GroupMsg getGroupMsg() {
        return groupMsg;
    }

    public void setGroupMsg(GroupMsg groupMsg) {
        this.groupMsg = groupMsg;
        setAccountInfo(groupMsg.getAccountInfo());
    }

    public GroupInfo getGroupInfo() {
        return groupMsg.getGroupInfo();
    }

    public long getGroup() {
        return getGroupInfo().getGroupCodeNumber();
    }

    public String getGroupString() {
        return getGroupInfo().getGroupCode();
    }

    @Override
    protected void sendMessage(String message) {
        try {
            getMsgSender().SENDER.sendGroupMsg(groupMsg, message);
        } catch (NoSuchElementException exception) {
            getMsgSender().SENDER.sendGroupMsg(groupMsg, "这条消息发不出去哦，因为包含了不存在的图片或 @ 了本群不存在的朋友");
            exception.printStackTrace();
        } catch (Exception exception) {
            getMsgSender().SENDER.sendPrivateMsg(getAccountInfo(),
                    "小明遇到了问题（" + exception.getClass().getSimpleName() + "）");
            getLogger().error("群 {} 中出现异常：", getGroupString());
            exception.printStackTrace();
        }
    }

    @Override
    public String getMessage() {
        return groupMsg.getMsg();
    }
}
