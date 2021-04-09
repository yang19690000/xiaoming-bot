package com.taixue.xiaominglexicons;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaominglexicons.autoreply.AutoReplyData;
import com.taixue.xiaominglexicons.listener.GroupMessageRepeater;
import com.taixue.xiaominglexicons.listener.PrivateMessageRepeater;

import java.io.File;

/**
 * @author Chuanwise
 */
public class XiaomingLexicons extends XiaomingPlugin {
    public static XiaomingLexicons instance;
    protected AutoReplyData data;

    @Override
    public void onEnable() {
        instance = this;
        getDataFolder().mkdirs();

        data = AutoReplyData.forFileOrNew(new File(getDataFolder(), "words.json"));
        logger.info("Xiaoming spawn!");
        registerGroupInteractors();
        logger.info("inteactor registed");
    }

    public AutoReplyData getData() {
        return data;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void registerGroupInteractors() {
        GroupMessageRepeater groupMessageRepeater = new GroupMessageRepeater();
        xiaomingBot.getCommandManager().register(new LexiconCommandExecutor(), this);
        xiaomingBot.getPrivateInteractorManager().register(new PrivateMessageRepeater(), this);
    }
}