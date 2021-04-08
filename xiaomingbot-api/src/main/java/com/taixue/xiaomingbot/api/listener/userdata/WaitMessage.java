package com.taixue.xiaomingbot.api.listener.userdata;

import com.taixue.xiaomingbot.util.AMethod;

public class WaitMessage {
    protected long latestTime;
    protected AMethod onSuccess, onFailure, onFinally;
    protected String waitFor;
    protected Thread thread;
    protected boolean result;

    public WaitMessage(long latestTime, String waitFor, AMethod onSuccess, AMethod onFailure, AMethod onFinally) {
        this.latestTime = latestTime;
        this.waitFor = waitFor;
        this.onFailure = onFailure;
        this.onSuccess = onSuccess;
        this.onFinally = onFinally;
        setThread();
    }

    public boolean check(String input) {
        result = System.currentTimeMillis() <= latestTime && input.equals(waitFor);
        thread.interrupt();
        return result;
    }

    protected void setThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(latestTime - System.currentTimeMillis());
                    onFailure.execute();
                }
                catch (InterruptedException e) {
                    if (result) {
                        onSuccess.execute();
                    }
                    else {
                        onFailure.execute();
                    }
                }
                finally {
                    onFinally.execute();
                }
            }
        };
    }

    public Thread getThread() {
        return thread;
    }
}
