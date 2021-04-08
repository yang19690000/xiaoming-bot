package com.taixue.xiaominglexicons;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaominglexicons.listener.GroupMessageRepeater;
import com.taixue.xiaominglexicons.listener.PrivateMessageRepeater;

/**
 * @author Chuanwise
 */
public class XiaomingLexicons extends XiaomingPlugin {
    @Override
    public void onEnable() {
        logger.info("Xiaoming spawn!");
        registerGroupInteractors();
        logger.info("inteactor registed");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void registerGroupInteractors() {
        GroupMessageRepeater groupMessageRepeater = new GroupMessageRepeater();
//        xiaomingBot.getGroupInteractorManager().register(groupMessageRepeater, this);
        xiaomingBot.getPrivateInteractorManager().register(new PrivateMessageRepeater(), this);
    }
}