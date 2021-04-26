package com.taixue.xiaoming.bot.api.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * json 文件保存的数据
 * @author Chuanwise
 */
public class JsonFileSavedData extends FileSavedData {
    @Override
    public void saveThrowsException() throws Exception {
        final File file = getFile();
        if (!file.exists() || file.isDirectory()) {
            file.createNewFile();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(getFile())) {
            JsonSerializerUtil.getInstance().writeValue(fileOutputStream, this);
        }
    }
}
