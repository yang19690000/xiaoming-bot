package com.taixue.xiaomingbot.api.group;

public class Group {
    private long code;
    private String alias;

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getAlias() {
        return alias;
    }

    public long getCode() {
        return code;
    }
}
