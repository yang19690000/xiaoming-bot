package com.taixue.xiaoming.bot.api.bot;

import com.taixue.xiaoming.bot.api.exception.MultipleBotException;
import com.taixue.xiaoming.bot.api.exception.XiaomingRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class XiaomingBotContainer {
    private static XiaomingBot INSTANCE = null;

    public static XiaomingBot getBot() {
        if (Objects.isNull(INSTANCE)) {
            throw new XiaomingRuntimeException();
        } else {
            return INSTANCE;
        }
    }

    public static void setBot(@NotNull XiaomingBot bot) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = bot;
        } else {
            throw new MultipleBotException();
        }
    }
}
