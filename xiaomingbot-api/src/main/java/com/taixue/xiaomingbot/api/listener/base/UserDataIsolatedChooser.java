package com.taixue.xiaomingbot.api.listener.base;

import com.taixue.xiaomingbot.api.listener.userdata.StatedUserData;
import com.taixue.xiaomingbot.api.listener.userdata.WaitMessage;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaomingbot.util.AMethod;
import love.forte.simbot.api.sender.MsgSender;

public abstract class UserDataIsolatedChooser<UserData extends StatedUserData> {
    protected UserDataIsolator<UserData> userDataIsolator = new UserDataIsolator() {
        @Override
        public UserData getDefaultUserData() {
            return newUserData();
        }
    };

    public abstract UserData newUserData();

    public void waitForRepeat(UserData userData, String waitFor, long maxTime, AMethod onSuccess) {
        waitForRepeat(userData, waitFor, maxTime, onSuccess, () -> {
            userData.toRememberedState();
        });
    }

    public void waitForRepeat(UserData userData, String waitFor, long maxTime, AMethod onSuccess, AMethod onFailure, AMethod onFinally) {
        userData.setRememberedState(userData.currentState());
        userData.toState("WaitForRepeat");
        WaitMessage waitMessage = new WaitMessage(System.currentTimeMillis() + maxTime, waitFor,
                onSuccess,
                onFailure,
                onFinally);
        userData.setWaitMessage(waitMessage);
        waitMessage.getThread().start();
    }

    public void waitForRepeat(UserData userData, String waitFor, long maxTime, AMethod onSuccess, AMethod onFailure) {
        waitForRepeat(userData, waitFor, maxTime, onSuccess, onFailure, () -> {});
    }

    public void onWaitForRepeat(UserData userData, MsgSender msgSender) {
        WaitMessage waitMessage = userData.getWaitMessage();
        waitMessage.check(userData.getMessage());
    }
}
