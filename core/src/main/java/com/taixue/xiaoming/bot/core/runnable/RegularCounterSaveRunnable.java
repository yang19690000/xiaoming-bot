package com.taixue.xiaoming.bot.core.runnable;

import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.util.TimeUtil;

public class RegularCounterSaveRunnable extends HostObjectImpl implements Runnable {
    public static final long DELTA_SAVE_TIME = TimeUtil.MINUTE_MINS * 10;
    @Override
    public void run() {
        try {
            while (true) {
                getXiaomingBot().getCounter().save();
                Thread.sleep(DELTA_SAVE_TIME);
            }
        } catch (InterruptedException exception) {
        }
    }
}
