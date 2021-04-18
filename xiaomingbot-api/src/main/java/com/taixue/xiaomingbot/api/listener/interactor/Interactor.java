package com.taixue.xiaomingbot.api.listener.interactor;

import com.taixue.xiaomingbot.api.exception.InteactorTimeoutException;
import com.taixue.xiaomingbot.api.listener.base.PluginUserDataIsolated;
import com.taixue.xiaomingbot.api.listener.userdata.DispatcherUser;
import com.taixue.xiaomingbot.api.listener.userdata.InteractorUser;
import com.taixue.xiaomingbot.api.listener.userdata.MessageWaiter;
import com.taixue.xiaomingbot.util.DateUtil;
import com.taixue.xiaomingbot.util.NoParameterMethod;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 所有交互器的超类
 * @param <UserData>
 */
public abstract class Interactor
        <UserData extends InteractorUser, DispatcherMessage extends DispatcherUser>
        extends PluginUserDataIsolated<UserData> {
    protected static final long NEXT_INPUT_TIMEOUT_TIME = DateUtil.MINUTE_MINS * 10;

    public void setFinished(long qq) {
        setFinished(userDataIsolator.getUserData(qq));
    }

    public void setFinished(UserData userData) {
        userData.setShouldExit(true);
    }

    public boolean isFinished(long qq) {
        return userDataIsolator.getUserData(qq).isShouldExit();
    }

    public abstract boolean interact(DispatcherMessage dispatcherMessage);

    public final boolean interact(UserData userData) throws Exception {
        MessageWaiter messageWaiter = userData.getMessageWaiter();
        if (Objects.isNull(messageWaiter)) {
            return onMessage(userData);
        }
        else {
            onGetNextInput(userData);
            userData.setMessageWaiter(null);
            return true;
        }
    }

    public abstract void onThrowable(Throwable throwable, UserData userData);

    public abstract boolean onMessage(UserData userData);

    public NoParameterMethod getTimeoutMethod(UserData userData, long timeOutTime, String defaultValue) {
        return () -> {
            userData.sendMessage("你已经{}没有理小明了，我们下次见哦", DateUtil.toTimeString(timeOutTime));
            setFinished(userData.getAccountInfo().getAccountCodeNumber());
            throw new InteactorTimeoutException(this);
        };
    }

    public String getNextInput(UserData userData, String defaultValue) {
        return getNextInput(userData, NEXT_INPUT_TIMEOUT_TIME, defaultValue);
    }

    @Nullable
    public String getNextInput(UserData userData, long timeOutTime) {
        return getNextInput(userData, timeOutTime, null);
    }

    @Nullable
    public String getNextInput(UserData userData) {
        return getNextInput(userData, NEXT_INPUT_TIMEOUT_TIME);
    }

    public final String getNextInput(UserData userData, long timeOutTime, String defaultValue) {
        return getNextInput(userData, timeOutTime, defaultValue, getTimeoutMethod(userData, timeOutTime, defaultValue));
    }

    /**
     * 获得下一个输入
     */
    public final String getNextInput(UserData userData, long timeOutTime, String defaultValue, NoParameterMethod method) {
        MessageWaiter messageWaiter = new MessageWaiter(System.currentTimeMillis() + timeOutTime, defaultValue);
        userData.setMessageWaiter(messageWaiter);
        try {
            synchronized (messageWaiter) {
                messageWaiter.wait(timeOutTime);
            }
        }
        catch (InterruptedException e) {
        }

        String value = messageWaiter.getValue();
        if (Objects.isNull(value)) {
            method.execute();
        }
        return value;
    }


    /**
     * 当获得了输入时
     * @param userData
     */
    public abstract void onGetNextInput(UserData userData);
}
