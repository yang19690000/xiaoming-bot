package com.taixue.xiaomingbot.api.base;

import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XiaomingObject {
    public abstract XiaomingBot getXiaomingBot();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Logger getLogger() {
        return logger;
    }
}
