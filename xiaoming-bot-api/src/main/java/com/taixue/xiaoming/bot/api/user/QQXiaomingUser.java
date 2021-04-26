package com.taixue.xiaoming.bot.api.user;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import kotlinx.coroutines.TimeoutCancellationException;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * @author Chuanwise
 */
public interface QQXiaomingUser extends XiaomingUser {
    AccountInfo getAccountInfo();

    MsgSender getMsgSender();

    default long getQQ() {
        return getAccountInfo().getAccountCodeNumber();
    }

    default String getQQString() {
        return getAccountInfo().getAccountCode();
    }

    default void sendPrivateMessage(String message) {
        try {
            getMsgSender().SENDER.sendPrivateMsg(getAccountInfo(), message);
        } catch (TimeoutCancellationException exception) {
        } catch (NoSuchElementException exception) {
            getMsgSender().SENDER.sendPrivateMsg(getAccountInfo(), "这条消息发不出去哦，因为包含了小明找不到的图片");
            exception.printStackTrace();
        } catch (Exception exception) {
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
}