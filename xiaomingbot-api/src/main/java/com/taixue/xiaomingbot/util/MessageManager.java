package com.taixue.xiaomingbot.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageManager {
    private final File directory;

    public MessageManager(File directory) {
        this.directory = directory;
    }

    @NotNull
    public String getMessage(String messageName) {
        File file = new File(directory, messageName + ".txt");
        if (!file.exists() || file.isDirectory()) {
            return messageName;
        }
        try {
            byte[] result = null;
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                result = new byte[fileInputStream.available()];
                fileInputStream.read(result);
            }
            if (result.length != 0) {
                return new String(result);
            }
            else {
                return messageName;
            }
        }
        catch (IOException ioException) {
            return messageName;
        }
    }

    public boolean hasMessage(String messageName) {
        File file = new File(directory, messageName + ".txt");
        return file.exists();
    }

    public boolean setMessage(String name, String content) {
        File file = new File(directory, name + ".txt");
        try {
            if (!file.exists() && !file.createNewFile()) {
                return false;
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(content.getBytes());
            }
            return true;
        }
        catch (IOException ioException) {
            return false;
        }
    }
}
