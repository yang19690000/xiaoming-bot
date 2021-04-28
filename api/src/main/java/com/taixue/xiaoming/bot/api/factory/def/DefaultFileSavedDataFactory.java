package com.taixue.xiaoming.bot.api.factory.def;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import org.jetbrains.annotations.NotNull;

public interface DefaultFileSavedDataFactory<T extends FileSavedData> {
    @NotNull
    T produce();
}
