package com.taixue.xiaomingbot.util;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

public class JSONFileData extends SavableFile {
    public static <T extends SavableFile> T forFile(File file, Class<T> dataClass) {
        T result = null;
        if (file.exists() && !file.isDirectory()) {
            try {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    result = JSON.parseObject(fileInputStream, dataClass);
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (Objects.nonNull(result)) {
            result.setFile(file);
        }
        return result;
    }

    public static <T extends SavableFile> T forFileOrNew(File file, Class<T> dataClass, FileDataFactory<T> factory) {
        T result = forFile(file, dataClass);
        if (Objects.isNull(result)) {
            result = factory.produce();
            result.setFile(file);
        }
        return result;
    }

    @Override
    public byte[] toFileData() {
        return JSON.toJSONString(this).getBytes();
    }
}
