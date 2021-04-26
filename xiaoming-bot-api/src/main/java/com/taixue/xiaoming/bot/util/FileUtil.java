package com.taixue.xiaoming.bot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public static final int COPY_PACK_SIZE = 1024;

    public static boolean copyResource(String path, File to)
            throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(to);
             InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(path)) {
            return copyResource(inputStream, fileOutputStream);
        }
    }

    public static boolean copyResource(InputStream inputStream, File to)
            throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(to)) {
            return copyResource(inputStream, fileOutputStream);
        }
    }

    public static boolean copyResource(InputStream inputStream, FileOutputStream fileOutputStream)
            throws IOException {
        byte[] bytes = new byte[COPY_PACK_SIZE];
        int len;
        while ((len = inputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, len);
        }
        return true;
    }
}
