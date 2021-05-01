package com.taixue.xiaoming.bot.api.user;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import kotlinx.coroutines.TimeoutCancellationException;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.results.FriendInfo;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Chuanwise
 */
public interface QQXiaomingUser extends XiaomingUser {
    AccountInfo getAccountInfo();

    default MsgSender getMsgSender() {
        return getXiaomingBot().getMsgSender();
    }

    default long getQQ() {
        return getAccountInfo().getAccountCodeNumber();
    }

    default String getQQString() {
        return getAccountInfo().getAccountCode();
    }

    default boolean sendPrivateMessage(long qq, String message, Object... arguments) {
        try {
            return sendPrivateMessage(getMsgSender().GETTER.getFriendInfo(qq), message, arguments);
        } catch (TimeoutCancellationException ignored) {
        } catch (Exception exception) {
            sendMessage("这条消息发不出去，因为无法发起聊天 " + getXiaomingBot().getEmojiManager().get("sad"));
        }
        return false;
    }

    default boolean sendPrivateMessage(AccountInfo accountInfo, String message, Object... arguments) {
        try {
            getMsgSender().SENDER.sendPrivateMsg(accountInfo, ArgumentUtil.replaceArguments(message, arguments));

            // 用于骗过编译器的抛出 IOException 的语句
            // 这里确实会抛出异常。当图片无法加载时
            if (false) {
                throw new IOException();
            }
            return true;
        } catch (TimeoutCancellationException ignored) {
        } catch (IOException exception) {
            sendMessage("这条消息发不出去 " + getXiaomingBot().getEmojiManager().get("sad") + "，因为相关图片载入失败");
            exception.printStackTrace();
        } catch (NoSuchElementException exception) {
            sendMessage("这条消息发不出去 " + getXiaomingBot().getEmojiManager().get("sad") + "，因为无法发起聊天");
            exception.printStackTrace();
        }
        return false;
    }

    default boolean sendPrivateMessage(String message, Object... arguments) {
        return sendPrivateMessage(getAccountInfo(), message, arguments);
    }

    String getMessage();

    @Override
    default String getName() {
        return getAccountInfo().getAccountRemarkOrNickname();
    }

    @Override
    default String getCompleteName() {
        return getName() + "（" + getQQString() + "）";
    }

    @Nullable
    default Account getAccount() {
        return XiaomingBot.getInstance().getAccountManager().getAccount(getQQ());
    }

    @NotNull
    default Account getOrPutAccount() {
        return XiaomingBot.getInstance().getAccountManager().getOrPutAccount(getQQ(), getAccountInfo().getAccountRemarkOrNickname());
    }

    @Override
    default boolean sendError(String message, Object... arguments) {
        return sendMessage(getXiaomingBot().getEmojiManager().get("error") + " " + message, arguments);
    }

    @Override
    default boolean sendWarning(String message, Object... arguments) {
        return sendMessage(getXiaomingBot().getEmojiManager().get("error") + " " + message, arguments);
    }
}