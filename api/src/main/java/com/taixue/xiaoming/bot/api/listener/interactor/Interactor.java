package com.taixue.xiaoming.bot.api.listener.interactor;

import com.taixue.xiaoming.bot.api.annotation.InteractMethod;
import com.taixue.xiaoming.bot.api.base.PluginObject;
import com.taixue.xiaoming.bot.api.listener.UserDataIslocated;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.GroupDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.PrivateDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.user.GroupInteractorUser;
import com.taixue.xiaoming.bot.api.listener.interactor.user.InteractorUser;
import com.taixue.xiaoming.bot.api.listener.interactor.user.PrivateInteractorUser;
import com.taixue.xiaoming.bot.util.NoParameterMethod;
import com.taixue.xiaoming.bot.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.util.Set;

public interface Interactor extends UserDataIslocated<Long, InteractorUser>, PluginObject {
    long NEXT_INPUT_TIMEOUT_TIME = TimeUtil.MINUTE_MINS * 10;

    void reloadInteractorDetails();

    Set<InteractorMethodDetail> getInteractorMethodDetails();

    void setWillExit(long qq);

    default void setWillExit(InteractorUser user) {
        user.shouldExit();
        onUserExit(user);
    }

    boolean isWillExit(long qq);

    default boolean isWillExit(InteractorUser user) {
        return isWillExit(user.getQQ());
    }

    /**
     * 判断是否当前轮到本交互器交互
     * @param user
     * @return
     */
    boolean willInteract(DispatcherUser user);

    void onUserExit(InteractorUser user);

    boolean interact(DispatcherUser user) throws Exception;

    @NotNull
    NoParameterMethod getDefaultTimeoutMethod(InteractorUser user,
                                              long timeOutTime);

    @Nullable
    default String getNextInput(InteractorUser user,
                                @Nullable String defaultValue) {
        return getNextInput(user, NEXT_INPUT_TIMEOUT_TIME, defaultValue);
    }

    @Nullable
    default String getNextInput(InteractorUser user,
                                long timeOutTime) {
        return getNextInput(user, timeOutTime, null);
    }

    @Nullable
    default String getNextInput(InteractorUser user) {
        return getNextInput(user, NEXT_INPUT_TIMEOUT_TIME);
    }

    @Nullable
    default String getNextInput(InteractorUser user,
                                long timeOutTime,
                                @Nullable String defaultValue) {
        return getNextInput(user, timeOutTime, defaultValue, getDefaultTimeoutMethod(user, timeOutTime));
    }

    @Nullable
    String getNextInput(InteractorUser user,
                        long timeOutTime,
                        @Nullable String defaultValue,
                        NoParameterMethod method);

    void onGetNextInput(InteractorUser user);

    Object onParameter(InteractorUser user, Parameter parameter);
}