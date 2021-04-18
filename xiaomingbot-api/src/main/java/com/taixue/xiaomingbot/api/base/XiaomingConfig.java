package com.taixue.xiaomingbot.api.base;

import com.taixue.xiaomingbot.util.JSONFileData;

public class XiaomingConfig extends JSONFileData {
    private int callCouter;

    public int getCallCouter() {
        return callCouter;
    }

    public void setCallCouter(int callCouter) {
        this.callCouter = callCouter;
    }

    public void increaseCallCounter() {
        setCallCouter(callCouter + 1);
        save();
    }
}
