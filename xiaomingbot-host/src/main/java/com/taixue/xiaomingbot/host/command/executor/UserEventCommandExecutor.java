package com.taixue.xiaomingbot.host.command.executor;

import com.taixue.xiaomingbot.api.command.*;
import com.taixue.xiaomingbot.api.user.User;
import com.taixue.xiaomingbot.api.user.UserEvent;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.util.AtUtil;
import com.taixue.xiaomingbot.util.CommandWordUtil;
import com.taixue.xiaomingbot.util.DateUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class UserEventCommandExecutor extends CommandExecutor {
    public final static String HISTORY_REGEX = "(历史(纪录|记录)?)";

    @CommandFormat(HISTORY_REGEX + " {who}")
    @RequiredPermission("history.others")
    public void onLookUserEvent(CommandSender sender,
                                @CommandParameter("who") String who) {
        final long qq = AtUtil.parseQQ(who);
        if (qq == -1) {
            sender.sendError("看起来 {} 并不是一个正确的用户哦", who);
            return;
        }
        final User user = getXiaomingBot().getUserManager().findUser(qq + "");
        if (Objects.isNull(user) || user.getEvents().isEmpty()) {
            sender.sendMessage("用户 {} 没有任何历史记录哦", who);
        }
        else {
            sender.sendMessage(getUserEvents(user));
        }
    }

    @CommandFormat(CommandWordUtil.PERSONAL_REGEX + HISTORY_REGEX)
    @RequiredPermission("history.me")
    public void onLookUserEvent(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("控制台没有历史记录哦");
            return;
        }
        final long qq;
        if (sender instanceof GroupCommandSender) {
            qq = ((GroupCommandSender) sender).getAccountCode();
        }
        else {
            qq = ((PrivateCommandSender) sender).getAccountCode();
        }
        final User user = getXiaomingBot().getUserManager().findUser(qq + "");
        if (Objects.isNull(user) || user.getEvents().isEmpty()) {
            sender.sendMessage("你没有任何历史记录哦");
        }
        else {
            sender.sendMessage(getUserEvents(user));
        }
    }

    public String getUserEvents(final User user) {
        final List<UserEvent> events = user.getEvents();
        StringBuilder builder = new StringBuilder("该用户共有").append(events.size()).append("条历史记录：");
        for (UserEvent event : events) {
            builder.append("\n");
            builder.append("[").append(DateUtil.format.format(event.getTime())).append("] ");
            if (event.getGroup() != 0) {
                builder.append("在群").append(event.getGroup()).append("中：");
            }
            builder.append(event.getContent());
        }
        return builder.toString();
    }
}
