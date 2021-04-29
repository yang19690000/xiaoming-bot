package com.taixue.xiaoming.bot.api.user;

import kotlinx.coroutines.TimeoutCancellationException;
import love.forte.simbot.api.message.containers.GroupInfo;
import net.mamoe.mirai.contact.BotIsBeingMutedException;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * 群聊中的小明使用者
 * @author Chuanwise
 */
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

            // 用于骗过编译器的抛出 IOException 的语句
            // 这里确实会抛出异常。当图片无法加载时
            if (false) {
                throw new IOException();
            }
        } catch (IOException | TimeoutCancellationException ignored) {
        } catch (NoSuchElementException exception) {
            sendMessage("这条消息发不出去呢 " + getXiaomingBot().getEmojiManager().get("sad") + "，因为 @ 了本群不存在的朋友");
            exception.printStackTrace();
        } catch (BotIsBeingMutedException exception) {
            sendMessage("小明在群" + getDescriptiveGroupName() + "中被禁言了哦，只能把消息发到这里来啦 " + getXiaomingBot().getEmojiManager().get("sad"));
            sendPrivateMessage(message);
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
