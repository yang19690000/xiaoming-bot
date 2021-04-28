package com.taixue.xiaoming.bot.api.limit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CallLimiter<Key, Record extends CallRecord> {
    @NotNull
    Map<Key, Record> getRecords();

    @NotNull
    CallLimitConfig getConfig();

    void setConfig(CallLimitConfig config);

    @Nullable
    Record getCallRecords(@NotNull Key key);

    @NotNull
    Record getOrPutCallRecords(@NotNull Key key);

    @NotNull
    Record newRecordInstance();

    default boolean uncallable(@NotNull Key key) {
        return isTooFastSoUncallable(key) || isTooManySoUncallable(key);
    }

    boolean isTooFastSoUncallable(@NotNull Key key);

    boolean isTooManySoUncallable(@NotNull Key key);

    void addCallRecord(@NotNull Key key);

    boolean shouldNotice(@NotNull Key key);

    void setNoticed(@NotNull Key key);
}
