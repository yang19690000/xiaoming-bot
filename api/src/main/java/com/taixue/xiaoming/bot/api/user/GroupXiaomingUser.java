package com.taixue.xiaoming.bot.api.user;

import kotlinx.coroutines.TimeoutCancellationException;
import love.forte.simbot.api.message.containers.GroupInfo;
import net.mamoe.mirai.contact.BotIsBeingMutedException;

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
            getMsgSender().SENDER.sendGroupMsg(getGroup(), message);
        } catch (TimeoutCancellationException exception) {
        } catch (NoSuchElementException exception) {
            getMsgSender().SENDER.sendGroupMsg(getGroup(), "这条消息发不出去呢 " + getXiaomingBot().getEmojiManager().get("sad") +
                    "，因为 @ 了本群不存在的朋友");
            exception.printStackTrace();
        } catch (BotIsBeingMutedException exception) {
            getMsgSender().SENDER.sendPrivateMsg(getQQ(), "小明在群" + getDescriptiveGroupName() + "中被禁言了哦，只能把消息发到这里来啦 " +
                    getXiaomingBot().getEmojiManager().get("sad"));
            sendPrivateMessage(message);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    default String getDescriptiveGroupName() {
        return getGroupInfo().getGroupName() + "（" + getGroupString() + "）";
    }

    @Override
    default void sendNoArgumentMessage(String message) {
        sendGroupMessage(message);
    }
}
