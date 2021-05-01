package com.taixue.xiaoming.bot.host.hook;

import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.host.XiaomingLauncher;

public class ShutdownHook extends HostObjectImpl implements Runnable {
    @Override
    public void run() {
        System.out.println("closing");
        final XiaomingLauncher instance = XiaomingLauncher.getInstance();
        instance.close(instance.getConsoleXiaomingUser());
        System.out.println("closed");
    }
}
