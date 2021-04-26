package com.taixue.xiaoming.bot.api.data;

import com.taixue.xiaoming.bot.api.base.XiaomingObject;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;

import java.io.File;

/**
 * @author Chuanwise
 */
public abstract class FileSavedData implements XiaomingObject {
    private transient File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public abstract void saveThrowsException() throws Exception;

    public boolean save() {
        try {
            saveThrowsException();
            return true;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        save();
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return XiaomingBot.getInstance();
    }
}
