package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequiredPermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;
import com.taixue.xiaoming.bot.api.command.executor.CommandParameter;
import com.taixue.xiaoming.bot.api.limit.CallLimitManager;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import com.taixue.xiaoming.bot.util.TimeUtil;

public class CallLimitCommandExecutor extends CommandExecutor {
    private static final String CALL_REGEX = "(调用|召唤|call)";
    private final CallLimitManager callLimitManager = getXiaomingBot().getCallLimitManager();
    private final CallLimitManager.Config config = callLimitManager.getConfig();

    @CommandFormat(CALL_REGEX + CommandWordUtil.LIMIT_REGEX)
    public void onLookLimit(final XiaomingUser user) {
        user.sendMessage("{}内只能召唤{}次小明，两次召唤的间隔不能小于{}（防止恶意刷屏）。",
                TimeUtil.toTimeString(config.getMaxDeltaCallTime()),
                config.getMaxCallNumber(),
                TimeUtil.toTimeString(config.getMinDeltaCallTime()));
    }

    @CommandFormat(CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (maxDeltaCallTime|间隔调用时长) {time}")
    @RequiredPermission("limit.maxDeltaCallTime")
    public void onSetMaxDeltaCallTime(final XiaomingUser user,
                                      @CommandParameter("time") final String timeString) {
        final long time = TimeUtil.parseTime(timeString);
        if (time == -1) {
            user.sendMessage("{}并不是一个合理的时间哦", timeString);
        } else {
            config.setMaxDeltaCallTime(time);
            callLimitManager.save();
            user.sendMessage("成功设置间隔调用时长为{}，在这段时间内最多召唤{}次小明",
                    TimeUtil.toTimeString(time),
                    config.getMaxCallNumber());
        }
    }

    @CommandFormat(CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (maxDeltaCallNumber|最大调用次数) {time}")
    @RequiredPermission("limit.maxDeltaCallNumber")
    public void onSetMaxDeltaCallNumber(final XiaomingUser user,
                                        @CommandParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        config.setMaxCallNumber(time);
        callLimitManager.save();
        user.sendMessage("成功设置最大调用次数为{}次，每{}内最多召唤这么多次小明",
                time,
                TimeUtil.toTimeString(config.getMaxDeltaCallTime()));
    }
}
