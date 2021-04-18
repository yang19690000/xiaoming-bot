package com.taixue.xiaomingbot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public static void copyResource(String path, File to)
            throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(to);
             InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(path)) {
            copyResource(inputStream, fileOutputStream);
        }
    }

    public static void copyResource(InputStream inputStream, File to)
            throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(to)) {
            copyResource(inputStream, fileOutputStream);
        }
    }

    public static void copyResource(InputStream inputStream, FileOutputStream fileOutputStream)
            throws IOException {
        byte[] bytes = new byte[1024];
        int len;
        while ((len = inputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, len);
        }
    }
}
