package com.taixue.xiaoming.bot.api.base;

import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 小明运行时的各大组件的基类
 * @author Chuanwise
 */
public class HostObject implements XiaomingObject {
    private transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return XiaomingBot.getInstance();
    }
}
