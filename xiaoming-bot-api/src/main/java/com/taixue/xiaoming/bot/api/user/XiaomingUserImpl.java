package com.taixue.xiaoming.bot.api.user;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import org.jetbrains.annotations.NotNull;

/**
 * User 是运行时的小明交互者，不一定会被存储
 * @author Chuanwise
 */
public abstract class XiaomingUserImpl extends HostObject implements XiaomingUser {
    @Override
    public void sendMessage(final String message,
                            final Object... arguments) {
        checkAndSendMessage(ArgumentUtil.replaceArguments(message, arguments));
    }

    @Override
    public void sendError(String message,
                          final Object... arguments) {
        message = ArgumentUtil.replaceArguments(message, arguments);
        checkAndSendMessage(getXiaomingBot().getEmojiManager().get("error") + message);
    }

    @Override
    public void sendWarning(String message,
                            final Object... arguments) {
        message = ArgumentUtil.replaceArguments(message, arguments);
        checkAndSendMessage(getXiaomingBot().getEmojiManager().get("warning") + message);
    }

    private void checkAndSendMessage(String message) {
        if (message.contains("{}")) {
            getLogger().error("当前发送的消息：{} 中包含未填充的参数", message);
        }
        sendMessage(message);
    }

    protected abstract void sendMessage(String message);

    @Override
    public boolean hasPermissions(@NotNull final String[] nodes) {
        for (String node : nodes) {
            if (!hasPermission(node)) {
                return false;
            }
        }
        return true;
    }
}
