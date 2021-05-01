package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.error.ErrorMessageManager;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.api.error.ErrorMessage;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import com.taixue.xiaoming.bot.util.TimeUtil;

import java.util.List;

public class DebugCommandExecutor extends CommandExecutorImpl {
    private final ErrorMessageManager errorMessageManager = getXiaomingBot().getErrorMessageManager();
    private final List<ErrorMessage> errorMessages = errorMessageManager.getErrorMessages();

    public void onShowErrorMessage(final XiaomingUser user,
                                   final ErrorMessage message) {
        StringBuilder builder = new StringBuilder()
                .append("【异常信息】").append("\n")
                .append(message.getMessage()).append("\n")
                .append("QQ：").append(message.getQqAlias()).append("（").append(message.getQq()).append("）").append("\n")
                .append("输入：").append(message.getInputs()).append("\n");
        if (message.getGroup() != 0) {
            builder.append("QQ群：").append(message.getGroupAlias()).append("（").append(message.getGroup()).append("）").append("\n");
        }
        builder.append("时间：").append(TimeUtil.FORMAT.format(message.getTime()));
        user.sendMessage(builder.toString());
    }

    @Command(CommandWordUtil.RECENT_REGEX + CommandWordUtil.EXCEPTION_REGEX)
    @RequirePermission("debug.exception")
    public void onLookLastException(final XiaomingUser user) {
        if (errorMessages.isEmpty()) {
            user.sendMessage("没有未经查看的异常哦");
        } else if (errorMessages.size() == 1) {
            onShowErrorMessage(user, errorMessages.get(0));
            errorMessages.clear();
            errorMessageManager.readySave();
        } else {
            StringBuilder builder = new StringBuilder()
                    .append("一共有 ").append(errorMessages.size()).append(" 个未经查看的异常");

            int index = 1;
            for (ErrorMessage errorMessage : errorMessages) {
                builder.append("\n").append(index++).append("、").append(errorMessage.getMessage());
            }
            user.sendMessage(builder.toString());
        }
    }

    @Command(CommandWordUtil.RECENT_REGEX + CommandWordUtil.EXCEPTION_REGEX + " {index}")
    @RequirePermission("debug.exception")
    public void onLookException(final XiaomingUser user,
                                @CommandParameter("index") final String indexString) {
        if (errorMessages.isEmpty()) {
            user.sendMessage("没有未经查看的异常哦");
        } else if (errorMessages.size() == 1) {
            user.sendMessage("只有一个未经处理的异常，直接使用 #近期异常 就可以啦");
            return;
        }

        final int index;
        if (indexString.matches("\\d+")) {
            index = Integer.parseInt(indexString);
        } else {
            user.sendError("{}并不是一个合理的数字哦", indexString);
            return;
        }

        if (index <= 0 || index > errorMessages.size()) {
            user.sendError("{}不对哦，它应该是介于 1 到 {} 之间的数字", indexString, errorMessages.size());
        } else {
            final ErrorMessage errorMessage = errorMessages.get(index - 1);
            onShowErrorMessage(user, errorMessage);
            errorMessages.remove(errorMessage);
            errorMessageManager.readySave();
        }
    }

    @Command(CommandWordUtil.CLEAR_REGEX + CommandWordUtil.RECENT_REGEX + CommandWordUtil.EXCEPTION_REGEX)
    @RequirePermission("debug.exception.clear")
    public void onClearException(final XiaomingUser user) {
        if (errorMessages.isEmpty()) {
            user.sendMessage("并没有需要清除的未经查看的异常哦");
        } else {
            errorMessages.clear();
            errorMessageManager.readySave();
            user.sendMessage("成功清除未经查看的异常");
        }
    }
}