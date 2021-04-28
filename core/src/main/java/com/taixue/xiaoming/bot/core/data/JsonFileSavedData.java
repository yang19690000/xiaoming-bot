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
        if (!file.exists() || file.isDirectory()) {
            final File parentDirectory = file.getParentFile();
            if (!parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }
            file.createNewFile();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(getFile())) {
            JsonSerializerUtil.getInstance().writeValue(fileOutputStream, this);
        }
    }
}
