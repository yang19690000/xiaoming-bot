package com.taixue.xiaoming.bot.api.factory.normal;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import com.taixue.xiaoming.bot.api.factory.def.DefaultFileSavedDataFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public interface FileSavedDataFactory {
    @Nullable
    default <T extends FileSavedData> T forFile(final File file,
                                                final Class<T> clazz) {
        try {
            if (file.isFile()) {
                return forFileThrowsException(file, clazz);
            } else {
                return null;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    <T extends FileSavedData> T forFileThrowsException(File file, Class<T> clazz)
            throws IOException;

    /**
     * 使用某指定工厂生成一个新的文件数据
     */
    @NotNull
    default <T extends FileSavedData> T forFileOrProduce(final File file,
                                                         final Class<T> clazz,
                                                         final DefaultFileSavedDataFactory<T> factory) {
        T result = forFile(file, clazz);
        if (Objects.isNull(result)) {
            result = factory.produce();
        }
        result.setFile(file);
        return result;
    }
}
