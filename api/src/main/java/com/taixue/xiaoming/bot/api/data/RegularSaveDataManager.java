package com.taixue.xiaoming.bot.api.data;

import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;

import java.util.Set;

public interface RegularSaveDataManager extends HostObject {
    void readySave(FileSavedData data);

    boolean save(XiaomingUser user);

    default boolean save() {
        return save(getXiaomingBot().getConsoleXiaomingUser());
    }

    Set<FileSavedData> getSaveSet();
}
