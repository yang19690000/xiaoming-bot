package com.taixue.xiaoming.bot.api.user;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import kotlinx.coroutines.TimeoutCancellationException;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.NoSuchElementException;

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

    default void sendPrivateMessage(String message, Object... arguments) {
        try {
            getMsgSender().SENDER.sendPrivateMsg(getAccountInfo(), ArgumentUtil.replaceArguments(message, arguments));

            // 用于骗过编译器的抛出 IOException 的语句
            // 这里确实会抛出异常。当图片无法加载时
            if (false) {
                throw new IOException();
            }
        } catch (IOException | TimeoutCancellationException ignored) {
        } catch (NoSuchElementException exception) {
            sendMessage("这条消息发不出去呢 " + getXiaomingBot().getEmojiManager().get("sad") + "，因为无法发起聊天或找不到相关资源");
            exception.printStackTrace();
        }
    }

    String getMessage();

    @Nullable
    default Account getAccount() {
        return XiaomingBot.getInstance().getAccountManager().getAccount(getQQ());
    }

    @NotNull
    default Account getOrPutAccount() {
        return XiaomingBot.getInstance().getAccountManager().getOrPutAccount(getQQ(), getAccountInfo().getAccountRemarkOrNickname());
    }

    void sendNoArgumentMessage(String message);

    @Override
    default void sendMessage(String message, Object... arguments) {
        sendNoArgumentMessage(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    default void sendError(String message, Object... arguments) {
        sendMessage(getXiaomingBot().getEmojiManager().get("error") + " " + message, arguments);
    }

    @Override
    default void sendWarning(String message, Object... arguments) {
        sendMessage(getXiaomingBot().getEmojiManager().get("error") + " " + message, arguments);
    }
}