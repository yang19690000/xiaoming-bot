package com.taixue.xiaomingbot.api.listener.userdata;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 状态型调度器或分派器的用户数据基础内容
 */
public class StatedUserData {
    protected List<String> states = new ArrayList<>();
    public static final String DEFAULT_STATE = "Default";
    protected WaitMessage waitMessage;
    protected long qq;
    protected String message;
    protected String rememberedState;

    public void setRememberedState(String rememberedState) {
        this.rememberedState = rememberedState;
    }

    public void toRememberedState() {
        toState(rememberedState);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getQQ() {
        return qq;
    }

    public void setQQ(long qq) {
        this.qq = qq;
    }

    public String currentState() {
        return states.get(states.size() - 1);
    }

    public void toState(String state) {
        states.add(state);
    }

    public boolean toLastState() {
        String lastState = getLastState();
        if (Objects.nonNull(lastState)) {
            toState(lastState);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public String getLastState() {
        if (states.size() > 1) {
            return states.get(states.size() - 2);
        } else {
            return null;
        }
    }

    public void toDefaultState() {
        toState(DEFAULT_STATE);
    }

    public String getStatesChain() {
        StringBuilder builder = new StringBuilder();
        if (states.size() > 0) {
            builder.append(states.get(0));
            for (int index = 1; index < states.size(); index++) {
                builder.append(" → ").append(states.get(index));
            }
        } else {
            builder.append("(empty)");
        }
        return builder.toString();
    }

    public WaitMessage getWaitMessage() {
        return waitMessage;
    }

    public void setWaitMessage(WaitMessage waitMessage) {
        this.waitMessage = waitMessage;
    }
}
