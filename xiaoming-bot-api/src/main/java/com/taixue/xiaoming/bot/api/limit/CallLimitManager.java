package com.taixue.xiaoming.bot.api.limit;

import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Chuanwise
 */
public class CallLimitManager extends JsonFileSavedData {
    public static class Config {
        private long minDeltaCallTime = TimeUtil.SECOND_MINS * 30;
        private long maxDeltaCallTime = TimeUtil.DAY_MINS;
        private int maxCallNumber = 10;

        public long getMinDeltaCallTime() {
            return minDeltaCallTime;
        }

        public void setMinDeltaCallTime(long minDeltaCallTime) {
            this.minDeltaCallTime = minDeltaCallTime;
        }

        public long getMaxDeltaCallTime() {
            return maxDeltaCallTime;
        }

        public void setMaxDeltaCallTime(long maxDeltaCallTime) {
            this.maxDeltaCallTime = maxDeltaCallTime;
        }

        public int getMaxCallNumber() {
            return maxCallNumber;
        }

        public void setMaxCallNumber(int maxCallNumber) {
            this.maxCallNumber = maxCallNumber;
        }
    }

    private final Config config = new Config();

    public static class UserCallRecord {
        private long[] records = null;
        private long lastNoticeTime;
        private int nextCallIndex = -1;

        public void setMaxDeltaCallNumber(final int maxDeltaCallNumber) {
            records = new long[maxDeltaCallNumber];
        }

        public long getLastNoticeTime() {
            return lastNoticeTime;
        }

        public void updateLastNoticeTime() {
            lastNoticeTime = System.currentTimeMillis();
        }

        public long getLastRecord(final Config config) {
            final int index = (nextCallIndex + config.getMaxCallNumber() - 1) % config.getMaxCallNumber();
            return records[index];
        }

        public void addNewCall(final Config config) {
            nextCallIndex = (nextCallIndex + config.getMaxCallNumber() + 1) % config.getMaxCallNumber();
            records[nextCallIndex] = System.currentTimeMillis();
        }

        public long getLatestRecord(final Config config) {
            final int index = (nextCallIndex + config.getMaxCallNumber() + 1) % config.getMaxCallNumber();
            return records[index];
        }

        public boolean callable(final Config config) {
            return !isTooFastSoUncallable(config) && !isTooManySoUncallable(config);
        }

        // 因为在一定时间内调用太多次而不能调用
        public boolean isTooManySoUncallable(final Config config) {
            return getLatestRecord(config) + config.getMaxDeltaCallTime() > System.currentTimeMillis();
        }

        // 因为两次调用之间太快而不能调用
        public boolean isTooFastSoUncallable(final Config config) {
            return System.currentTimeMillis() < getLastRecord(config) + config.getMinDeltaCallTime();
        }
    }

    public static class UserCallRecords {
        private UserCallRecord groupRecords = new UserCallRecord();
        private UserCallRecord privateRecords = new UserCallRecord();

        public void setMaxDeltaCallNumber(final int maxDeltaCallNumber) {
            groupRecords.setMaxDeltaCallNumber(maxDeltaCallNumber);
            privateRecords.setMaxDeltaCallNumber(maxDeltaCallNumber);
        }

        public UserCallRecord getGroupRecords() {
            return groupRecords;
        }

        public UserCallRecord getPrivateRecords() {
            return privateRecords;
        }
    }

    private final Map<Long, UserCallRecords> records = new HashMap<>();

    @Nullable
    public UserCallRecords getUserCallRecords(final long qq) {
        return records.get(qq);
    }

    @NotNull
    public UserCallRecords getOrPutUserCallRecords(final long qq) {
        UserCallRecords groupUserCallRecord = getUserCallRecords(qq);
        if (Objects.isNull(groupUserCallRecord)) {
            groupUserCallRecord = new UserCallRecords();
            groupUserCallRecord.setMaxDeltaCallNumber(config.getMaxCallNumber());
            records.put(qq, groupUserCallRecord);
        }
        return groupUserCallRecord;
    }

    public Config getConfig() {
        return config;
    }
}
