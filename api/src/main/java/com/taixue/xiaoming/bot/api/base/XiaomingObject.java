package com.taixue.xiaoming.bot.api.base;

import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 所有和小明相关的组件的基类
 * @author Chuanwise
 */
public interface XiaomingObject {
    default Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    XiaomingBot getXiaomingBot();
}