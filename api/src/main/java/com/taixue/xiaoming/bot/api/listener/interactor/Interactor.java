package com.taixue.xiaoming.bot.api.listener.interactor;

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

import java.util.Set;

public interface Interactor extends UserDataIslocated<Long, InteractorUser>, PluginObject {
    long NEXT_INPUT_TIMEOUT_TIME = TimeUtil.MINUTE_MINS * 10;

    void reloadInteractorDetails();

    Set<InteractorMethodDetail> getInteractorMethodDetails();

    void setWillExit(long qq);

    void setWillExit(@NotNull InteractorUser user);

    boolean isWillExit(long qq);

    boolean isWillExit(@NotNull InteractorUser user);

    /**
     * 判断是否当前轮到本交互器交互
     * @param user
     * @return
     */
    boolean willInteract(@NotNull DispatcherUser user);

    void onUserIn(@NotNull DispatcherUser user);

    void onGroupUserIn(@NotNull GroupDispatcherUser user);

    void onGroupUserIn(@NotNull GroupInteractorUser user);

    void onPrivateUserIn(@NotNull PrivateDispatcherUser user);

    void onPrivateUserIn(@NotNull PrivateInteractorUser user);

    void onUserExit(@NotNull InteractorUser user);

    boolean interact(@NotNull DispatcherUser user) throws Exception;

    boolean interact(@NotNull InteractorUser user) throws Exception;

    @NotNull
    NoParameterMethod getDefaultTimeoutMethod(@NotNull InteractorUser user,
                                              long timeOutTime);

    @Nullable
    default String getNextInput(@NotNull final InteractorUser user,
                                     @Nullable String defaultValue) {
        return getNextInput(user, NEXT_INPUT_TIMEOUT_TIME, defaultValue);
    }

    @Nullable
    default String getNextInput(@NotNull final InteractorUser user,
                                     long timeOutTime) {
        return getNextInput(user, timeOutTime, null);
    }

    @Nullable
    default String getNextInput(@NotNull final InteractorUser user) {
        return getNextInput(user, NEXT_INPUT_TIMEOUT_TIME);
    }

    @Nullable
    default String getNextInput(@NotNull final InteractorUser user,
                                     long timeOutTime,
                                     @Nullable String defaultValue) {
        return getNextInput(user, timeOutTime, defaultValue, getDefaultTimeoutMethod(user, timeOutTime));
    }

    @Nullable
    String getNextInput(@NotNull InteractorUser user,
                        long timeOutTime,
                        @Nullable String defaultValue,
                        @NotNull NoParameterMethod method);

    void onGetNextInput(@NotNull InteractorUser user);
}
