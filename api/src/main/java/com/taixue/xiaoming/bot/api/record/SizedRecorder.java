package com.taixue.xiaoming.bot.api.record;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SizedRecorder<DataType> {
    void add(final DataType value, final int size);

    @Nullable
    DataType latest();

    @Nullable
    DataType earlyest();

    @NotNull
    DataType[] list();
}
