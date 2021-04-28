package com.taixue.xiaoming.bot.api.user;

import kotlinx.coroutines.TimeoutCancellationException;
import love.forte.simbot.api.message.containers.GroupInfo;

import java.util.NoSuchElementException;

public interface GroupXiaomingUser extends QQXiaomingUser {
    GroupInfo getGroupInfo();

    default long getGroup() {
        return getGroupInfo().getGroupCodeNumber();
    }

    default String getGroupString() {
        return getGroupInfo().getGroupCode();
    }

    default void sendGroupMessage(String message) {
        try {
            getMsgSender().SENDER.sendGroupMsg(getGroupInfo(), message);
        } catch (TimeoutCancellationException exception) {
        } catch (NoSuchElementException exception) {
            getMsgSender().SENDER.sendGroupMsg(getGroupInfo(), "这条消息发不出去哦，因为包含了不存在的图片或 @ 了本群不存在的朋友");
            exception.printStackTrace();
        } catch (Exception exception) {
            getMsgSender().SENDER.sendPrivateMsg(getAccountInfo(),
                    "小明遇到了问题（" + exception.getClass().getSimpleName() + "）");
            exception.printStackTrace();
        }
    }
}
