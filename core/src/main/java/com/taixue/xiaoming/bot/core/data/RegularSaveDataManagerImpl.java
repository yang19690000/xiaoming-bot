package com.taixue.xiaoming.bot.core.data;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import com.taixue.xiaoming.bot.api.data.RegularSaveDataManager;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegularSaveDataManagerImpl extends HostObjectImpl implements RegularSaveDataManager {
    final Set<FileSavedData> saveSet = new HashSet<>();

    @Override
    public void readySave(FileSavedData data) {
        synchronized (saveSet) {
            saveSet.add(data);
        }
    }

    @Override
    public boolean save(XiaomingUser user) {
        synchronized (saveSet) {
            if (!saveSet.isEmpty()) {
                List<FileSavedData> failList = new ArrayList<>();
                for (FileSavedData data : saveSet) {
                    if (!data.save()) {
                        failList.add(data);
                    }
                }
                saveSet.addAll(failList);

                if (failList.isEmpty()) {
                    user.sendMessage("成功保存了 {} 个文件", saveSet.size());
                } else {
                    StringBuilder builder = new StringBuilder()
                            .append("小明尝试保存 " + saveSet.size() + " 个文件，但 " + failList.size() + " 个文件保存失败：");
                    for (FileSavedData data : failList) {
                        builder.append("\n").append(data.getFile().getPath());
                    }
                    user.sendError(builder.toString());
                }
                saveSet.clear();
                return !failList.isEmpty();
            }
        }
        return false;
    }

    @Override
    public Set<FileSavedData> getSaveSet() {
        return saveSet;
    }
}
