package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequiredPermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;
import com.taixue.xiaoming.bot.api.command.executor.CommandParameter;
import com.taixue.xiaoming.bot.api.emoji.EmojiManager;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;

import java.util.*;

public class EmojiCommandExecutor extends CommandExecutor {
    private final EmojiManager emojiManager = getXiaomingBot().getEmojiManager();

    @Override
    public String getHelpPrefix() {
        return "(表情|emoji)";
    }

    @CommandFormat("表情 {key}")
    @RequiredPermission("emoji.look")
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

    @CommandFormat("表情")
    @RequiredPermission("emoji.list")
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

    @CommandFormat("表情 {key} 添加 {remain}")
    @RequiredPermission("emoji.add")
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
        emojiManager.save();
    }
}
