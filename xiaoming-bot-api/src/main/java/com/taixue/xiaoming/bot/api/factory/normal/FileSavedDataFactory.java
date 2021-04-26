package com.taixue.xiaoming.bot.api.factory.normal;

import com.taixue.xiaoming.bot.api.data.FileSavedData;
import com.taixue.xiaoming.bot.api.factory.def.DefaultFileSavedDataFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

/**
 * 以文件的形式保存的数据工厂
 * @author Chuanwise
 */
public abstract class FileSavedDataFactory {
    @Nullable
    public <T extends FileSavedData> T forFile(final File file,
                                               final Class<T> clazz) {
        try {
            final T result = forFileThrowsException(file, clazz);
            return result;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Nullable
    public abstract <T extends FileSavedData> T forFileThrowsException(final File file,
                                             final Class<T> clazz) throws Exception;

    /**
     * 使用某指定工厂生成一个新的文件数据
     */
    @NotNull
    public <T extends FileSavedData> T forFileOrProduce(final File file,
                              final Class<T> clazz,
                              final DefaultFileSavedDataFactory<T> factory) {
        T result = null;
        if (file.exists()) {
            result = forFile(file, clazz);
        }
        if (Objects.isNull(result)) {
            result = factory.produce();
        }
        result.setFile(file);
        return result;
    }
}
