package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.util.TimeUtil;

public class UsefulCommandExecutor extends CommandExecutorImpl {
    @Command("(QQ|qq) {qq}")
    public void onQQ(final XiaomingUser user,
                     @CommandParameter("qq") final long qq) {
        user.sendMessage("他的 QQ 是：{}", qq);
    }

    @Command("(之后|after) {time}")
    public void onAfter(final XiaomingUser user,
                        @CommandParameter("time") final String timeString) {
        final long time = TimeUtil.parseTime(timeString);
        if (time == -1) {
            user.sendError("{}并不是一个合理的时间哦", timeString);
        } else {
            user.sendMessage("{}之后是{}", TimeUtil.toTimeString(time), TimeUtil.FORMAT.format(System.currentTimeMillis() + time));
        }
    }
}
