package com.taixue.xiaoming.bot.core.config;

import com.taixue.xiaoming.bot.api.config.BotAccount;
import com.taixue.xiaoming.bot.api.exception.XiaomingSequrityException;

public class BotAccountImpl implements BotAccount {
    private long qq;
    private String password;
    private transient boolean passwordGetted = false;

    public BotAccountImpl() {
    }

    public BotAccountImpl(long qq, String password) {
        this.qq = qq;
        this.password = password;
    }

    @Override
    public long getQq() {
        return qq;
    }

    @Override
    public String getPassword() {
        if (passwordGetted) {
            throw new XiaomingSequrityException();
        } else {
            passwordGetted = true;
            return password;
        }
    }

    public void setQq(long qq) {
        this.qq = qq;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}