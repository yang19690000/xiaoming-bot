package com.taixue.xiaoming.bot.core.data;

import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.api.data.FileSavedData;

import java.io.File;

/**
 * @author Chuanwise
 */
public abstract class FileSavedDataImpl implements FileSavedData {
    private transient File file;

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean save() {
        try {
            saveThrowsException();
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        readySave();
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return XiaomingBot.getInstance();
    }
}