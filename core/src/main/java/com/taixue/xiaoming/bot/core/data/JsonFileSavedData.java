package com.taixue.xiaoming.bot.core.data;

import com.taixue.xiaoming.bot.core.data.FileSavedDataImpl;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * json 文件保存的数据
 * @author Chuanwise
 */
public class JsonFileSavedData extends FileSavedDataImpl {
    @Override
    public void saveThrowsException() throws Exception {
        final File file = getFile();
        if (!file.isFile()) {
            file.createNewFile();
        }
        synchronized (this) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(getFile())) {
                JsonSerializerUtil.getInstance().writeValue(fileOutputStream, this);
            }
        }
    }
}
