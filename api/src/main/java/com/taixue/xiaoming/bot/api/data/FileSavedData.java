package com.taixue.xiaoming.bot.api.data;

import com.taixue.xiaoming.bot.api.base.XiaomingObject;

import java.io.File;

public interface FileSavedData extends XiaomingObject {
    File getFile();

    void setFile(File file);

    void saveThrowsException() throws Exception;

    default boolean readySave() {
        getXiaomingBot().getRegularSaveDataManager().readySave(this);
        return true;
    }

    boolean save();
}
