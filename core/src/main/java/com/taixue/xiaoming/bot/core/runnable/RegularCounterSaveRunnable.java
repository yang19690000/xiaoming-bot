package com.taixue.xiaoming.bot.core.runnable;

import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.util.TimeUtil;

public class RegularCounterSaveRunnable extends HostObjectImpl implements Runnable {
    public static final long DELTA_SAVE_TIME = TimeUtil.HOUR_MINS;

    @Override
    public void run() {
        try {
            while (true) {
                getXiaomingBot().getRegularSaveDataManager().save();
                Thread.sleep(DELTA_SAVE_TIME);
            }
        } catch (InterruptedException exception) {
        }
    }
}