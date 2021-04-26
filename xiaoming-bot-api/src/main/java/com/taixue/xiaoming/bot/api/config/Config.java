package com.taixue.xiaoming.bot.api.config;

import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.api.exception.XiaomingSequrityException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config extends JsonFileSavedData {
    public static class BotAccount {
        private long qq;
        private String password;

        public BotAccount() {}

        public BotAccount(long qq, String password) {
            this.qq = qq;
            this.password = password;
        }

        public long getQq() {
            return qq;
        }

        public String getPassword() {
            if (Objects.isNull(this.password)) {
                throw new XiaomingSequrityException();
            } else {
                final String password = this.password;
                this.password = null;
                return password;
            }
        }
    }

    private volatile boolean debug = false;
    private volatile long callCounter = 0;
    private List<BotAccount> accounts = new ArrayList<>();

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public long getCallCounter() {
        return callCounter;
    }

    public void increaseCallCounter() {
        callCounter++;
        save();
    }

    public List<BotAccount> getAccounts() {
        return accounts;
    }
}
