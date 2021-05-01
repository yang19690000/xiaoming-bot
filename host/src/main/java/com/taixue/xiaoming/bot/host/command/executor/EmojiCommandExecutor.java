package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.emoji.EmojiManager;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;

import java.util.*;

public class EmojiCommandExecutor extends CommandExecutorImpl {
    private final EmojiManager emojiManager = getXiaomingBot().getEmojiManager();

    @Override
    public String getHelpPrefix() {
        return "(表情|emoji)";
    }

    @Command("表情 {key}")
    @RequirePermission("emoji.look")
    public void onListEmoji(final XiaomingUser user,
                            @CommandParameter("key") final String key) {
        final Set<String> set = emojiManager.getSet(key);
        if (Objects.isNull(set) || set.isEmpty()) {
            user.sendMessage("小明没有收录任何有关{}的表情哦", key);
        }
        else {
            user.sendMessage("小明收录的{}类表情有：{}", key, set);
        }
    }

    @Command("表情")
    @RequirePermission("emoji.list")
    public void onListEmoji(final XiaomingUser user) {
        final Set<Map.Entry<String, Set<String>>> entries = emojiManager.getValues().entrySet();
        if (entries.isEmpty()) {
            user.sendMessage("小明没有收录任何表情哦");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("小明收录了").append(entries.size()).append("种表情：");
        for (Map.Entry<String, Set<String>> entry : entries) {
            builder.append("\n").append(entry.getKey()).append("：").append(entry.getValue());
        }
        user.sendMessage(builder.toString());
    }

    @Command("表情 {key} 添加 {remain}")
    @RequirePermission("emoji.add")
    public void onAddEmoji(final XiaomingUser user,
                           @CommandParameter("key") final String key,
                           @CommandParameter("remain") final String emoji) {
        if (emoji.isEmpty()) {
            user.sendError("添加的表情不能为空");
            return;
        }
        final Map<String, Set<String>> map = emojiManager.getValues();
        Set<String> emojiSet = map.get(key);
        if (Objects.isNull(emojiSet)) {
            emojiSet = new HashSet<>();
            map.put(key, emojiSet);
        }
        emojiSet.add(emoji);
        user.sendMessage("成功添加了{}类型表情：{}", key, emoji);
        emojiManager.readySave();
    }
}
