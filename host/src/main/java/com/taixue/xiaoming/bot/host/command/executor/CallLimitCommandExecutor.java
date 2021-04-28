package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.limit.CallLimitConfig;
import com.taixue.xiaoming.bot.api.limit.UserCallLimitManager;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import com.taixue.xiaoming.bot.util.TimeUtil;

public class CallLimitCommandExecutor extends CommandExecutorImpl {
    private static final String CALL_REGEX = "(调用|召唤|call)";
    private final UserCallLimitManager callLimitManager = getXiaomingBot().getUserCallLimitManager();
    private final CallLimitConfig groupCallLimitConfig = callLimitManager.getGroupCallLimiter().getConfig();

    @Command(CALL_REGEX + CommandWordUtil.LIMIT_REGEX)
    public void onLookLimit(final XiaomingUser user) {
        user.sendMessage("{}内只能召唤{}次小明，两次召唤的间隔不能小于{}（防止恶意刷屏）。",
                TimeUtil.toTimeString(groupCallLimitConfig.getPeriod()),
                groupCallLimitConfig.getMaxCallNumber(),
                TimeUtil.toTimeString(groupCallLimitConfig.getCoolDown()));
    }

    @Command(CommandWordUtil.GROUP_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (间隔调用时长|period) {time}")
    @RequirePermission("limit.period")
    public void onSetMaxDeltaCallTime(final XiaomingUser user,
                                      @CommandParameter("time") final String timeString) {
        final long time = TimeUtil.parseTime(timeString);
        if (time == -1) {
            user.sendMessage("{}并不是一个合理的时间哦", timeString);
        } else {
            groupCallLimitConfig.setPeriod(time);
            callLimitManager.save();
            user.sendMessage("成功设置间隔调用时长为{}，在这段时间内最多召唤{}次小明",
                    TimeUtil.toTimeString(time),
                    groupCallLimitConfig.getMaxCallNumber());
        }
    }

    @Command(CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (最大调用次数|maxCallNumber) {time}")
    @RequirePermission("limit.maxCallNumber")
    public void onSetMaxDeltaCallNumber(final XiaomingUser user,
                                        @CommandParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        groupCallLimitConfig.setMaxCallNumber(time);
        callLimitManager.save();
        callLimitManager.getGroupCallLimiter().getRecords().clear();
        user.sendMessage("成功设置最大调用次数为{}次，已清空所有调用纪录。未来每{}内最多召唤这么多次小明",
                time,
                TimeUtil.toTimeString(groupCallLimitConfig.getPeriod()));
    }

    @Command(CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (冷却时间|冷却|cooldown) {time}")
    @RequirePermission("limit.cooldown")
    public void onSetCoolDown(final XiaomingUser user,
                              @CommandParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        if (time > TimeUtil.SECOND_MINS * 30) {
            user.sendError("过长的调用冷却时间可能会影响使用体验");
        }
        groupCallLimitConfig.setCoolDown(time);
        callLimitManager.save();
        user.sendMessage("成功设置小明的召唤冷却时间为{}", TimeUtil.toTimeString(groupCallLimitConfig.getPeriod()));
    }
}
