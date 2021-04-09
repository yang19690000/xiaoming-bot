package com.taixue.xiaominglexicons;

import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.api.command.*;
import com.taixue.xiaominglexicons.autoreply.AutoReplyItem;
import love.forte.simbot.api.sender.MsgSender;

import java.util.Objects;

public class LexiconCommandExecutor extends CommandExecutor {
    @CommandFormat("查看全局词条 {key}")
    @RequiredPermission("reply.global.look")
    public void onLookGlobalReply(CommandSender sender,
                                  @CommandParameter("key") String key) {
        AutoReplyItem globalValue = XiaomingLexicons.plugin.getData().getGlobalValue(key);
        if (Objects.isNull(globalValue)) {
            sender.sendMessage("公共词库中没有词条：{}", key);
        }
        else {
            StringBuilder builder = new StringBuilder("公共词条 " + key + " 中的随机回答有：");
            int index = 1;
            for (String reply: globalValue.answers) {
                builder.append("\n" + (index++) + "、").append(reply);
            }
            sender.sendMessage(builder.toString());
        }
    }
}