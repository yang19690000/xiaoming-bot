package com.taixue.xiaoming.bot.api.factory.normal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taixue.xiaoming.bot.api.data.FileSavedData;
import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * JSON 文件形式保存的数据的工厂
 * @author Chuanwise
 */
public class JsonFileSavedDataFactory extends FileSavedDataFactory {
    @Nullable
    @Override
    public <T extends FileSavedData> T forFileThrowsException(File file, Class<T> clazz)
            throws IOException {
        T result = null;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            result = JsonSerializerUtil.getInstance().readValue(fileInputStream, clazz);
        }
        if (Objects.nonNull(result)) {
            result.setFile(file);
        }
        return result;
    }
}
