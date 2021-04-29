package com.taixue.xiaoming.bot.core.limit;

import com.taixue.xiaoming.bot.api.limit.CallLimitConfig;
import com.taixue.xiaoming.bot.api.limit.CallLimiter;
import com.taixue.xiaoming.bot.api.limit.CallRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Chuanwise
 */
public abstract class CallLimiterImpl<Key, Value extends CallRecord> implements CallLimiter<Key, Value> {
    private transient CallLimitConfig config = null;

    private final Map<Key, Value> records = new HashMap<>();

    public CallLimiterImpl() {}

    public CallLimiterImpl(@NotNull CallLimitConfig config) {
        setConfig(config);
    }

    @Override
    @NotNull
    public Map<Key, Value> getRecords() {
        return records;
    }

    @Override
    @NotNull
    public CallLimitConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(CallLimitConfig config) {
        this.config = config;
    }

    @Override
    @Nullable
    public Value getCallRecords(@NotNull final Key key) {
        return records.get(key);
    }

    @Override
    @NotNull
    public Value getOrPutCallRecords(@NotNull final Key key) {
        Value groupUserCallRecord = getCallRecords(key);
        if (Objects.isNull(groupUserCallRecord)) {
            groupUserCallRecord = newRecordInstance();
            records.put(key, groupUserCallRecord);
        }
        return groupUserCallRecord;
    }

    @Override
    public boolean isTooFastSoUncallable(@NotNull final Key key) {
        final Value userCallRecord = getCallRecords(key);
        if (Objects.isNull(userCallRecord)) {
            return false;
        } else {
            return config.getCoolDown() > 0 && userCallRecord.isTooFastSoUncallable(config);
        }
    }

    @Override
    public boolean isTooManySoUncallable(@NotNull final Key key) {
        final Value userCallRecord = getCallRecords(key);
        if (Objects.isNull(userCallRecord)) {
            return false;
        } else {
            return config.getTop() > 0 && userCallRecord.isTooManySoUncallable(config);
        }
    }

    @Override
    public void addCallRecord(@NotNull final Key key) {
        getOrPutCallRecords(key).addNewCall(config);
    }

    @Override
    public boolean shouldNotice(@NotNull Key key) {
        final Value records = getCallRecords(key);
        return Objects.nonNull(records) && records.getLastNoticeTime() + config.getDeltaNoticeTime() < System.currentTimeMillis();
    }

    @Override
    public void setNoticed(@NotNull Key key) {
        getOrPutCallRecords(key).updateLastNoticeTime();
    }
}
