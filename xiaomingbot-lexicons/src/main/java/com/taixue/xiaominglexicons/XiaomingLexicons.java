package com.taixue.xiaominglexicons;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaominglexicons.autoreply.AutoReplyData;
import com.taixue.xiaominglexicons.hook.LexiconsHookHolder;
import com.taixue.xiaominglexicons.listener.GroupMessageRepeater;
import com.taixue.xiaominglexicons.listener.PrivateMessageRepeater;

import javax.management.ReflectionException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @author Chuanwise
 */
public class XiaomingLexicons extends XiaomingPlugin {
    public static XiaomingLexicons plugin;
    protected AutoReplyData data;

    @Override
    public void onEnable() {
        plugin = this;
        getDataFolder().mkdirs();

        LexiconsHookHolder lexicons = null;
        try {
            lexicons = hook("Lexicons", LexiconsHookHolder.class);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (Objects.isNull(lexicons)) {
            // hook failure
        }
        else {
            lexicons.action();
        }

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