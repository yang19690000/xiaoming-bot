package com.taixue.xiaoming.bot.api.user;

import catcode.CatCodeUtil;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import kotlinx.coroutines.TimeoutCancellationException;
import love.forte.simbot.api.message.containers.AccountContainer;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.results.GroupFullInfo;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import net.mamoe.mirai.contact.BotIsBeingMutedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

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

    default GroupFullInfo getGroupFullInfo() {
        return getMsgSender().GETTER.getGroupInfo(this::getGroupInfo);
    }

    @Nullable
    default GroupFullInfo getGroupFullInfo(long group) {
        try {
            return getMsgSender().GETTER.getGroupInfo(group);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    default boolean sendPrivateMessage(long qq, String message, Object... arguments) {
        // 尝试寻找本群的成员
        try {
            final GroupMemberInfo memberInfo = getMsgSender().GETTER.getMemberInfo(getGroup(), qq);
            return sendPrivateMessage(memberInfo, message, arguments);
        } catch (TimeoutCancellationException ignored) {
        } catch (Exception exception) {
        }
        // 尝试寻找自己的好友
        try {
            return sendPrivateMessage(getMsgSender().GETTER.getFriendInfo(qq), message, arguments);
        } catch (TimeoutCancellationException ignored) {
        } catch (Exception exception) {
            sendMessage("这条消息发不出去，因为无法发起聊天 " + getXiaomingBot().getEmojiManager().get("sad"));
        }
        return false;
    }

    default boolean sendGroupMessage(long group, String message, Object... arguments) {
        try {
            return sendGroupMessage(getMsgSender().GETTER.getGroupInfo(group), message, arguments);
        } catch (Exception exception) {
            return false;
        }
    }

    default boolean sendGroupMessage(GroupInfo groupInfo, String message, Object... arguments) {
        message = ArgumentUtil.replaceArguments(message, arguments);
        try {
            getMsgSender().SENDER.sendGroupMsg(groupInfo, message);

            // 用于骗过编译器的抛出 IOException 的语句
            // 这里确实会抛出异常。当图片无法加载时
            if (false) {
                throw new IOException();
            }
            return true;
        } catch (IOException | TimeoutCancellationException ignored) {
        } catch (NoSuchElementException exception) {
            sendMessage("这条消息发不出去 " + getXiaomingBot().getEmojiManager().get("sad") + "，因为 @ 了本群不存在的朋友");
            exception.printStackTrace();
        } catch (BotIsBeingMutedException exception) {
            final String groupName = groupInfo.getGroupName() + "（" + groupInfo.getGroupName() + "）";
            sendPrivateMessage("小明在群" + groupName + "中被禁言了哦，只能把消息发到这里来啦 " + getXiaomingBot().getEmojiManager().get("sad"));
            sendPrivateMessage(message);
        }
        return false;
    }

    default boolean sendGroupMessage(String message, Object... arguments) {
        return sendGroupMessage(getGroupInfo(), message, arguments);
    }

    @Override
    default boolean sendMessage(String message, Object... arguments) {
        return sendGroupMessage(CatCodeUtil.getInstance().getStringTemplate().at(getQQString()) + " " + message, arguments);
    }
}
