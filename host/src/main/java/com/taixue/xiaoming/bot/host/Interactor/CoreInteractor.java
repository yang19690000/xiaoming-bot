package com.taixue.xiaoming.bot.host.Interactor;

import com.taixue.xiaoming.bot.api.annotation.InteractMethod;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.user.InteractorUser;
import com.taixue.xiaoming.bot.core.listener.interactor.InteractorImpl;

import java.util.Objects;

public class CoreInteractor extends InteractorImpl {
    @Override
    public boolean willInteract(DispatcherUser user) {
        return Objects.equals(user.getMessage(), "测试消息");
    }

    @InteractMethod
    public void onMessage(InteractorUser user) {
        user.sendMessage("your msg is {}", user.getMsgSender());

        while (true) {
            final String nextInput = getNextInput(user);
            if (Objects.equals(nextInput, "quit")) {
                break;
            } else {
                user.sendMessage("msg is {}", user.getMessage());
            }
        }
    }
}
