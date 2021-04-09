package com.taixue.xiaomingbot.api.group;

public class Group {
    protected long code;
    protected String alias;

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
