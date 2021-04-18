package com.taixue.xiaomingbot.util;

import java.io.File;
import java.io.FileOutputStream;

public abstract class SavableFile {
    private transient File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void saveThrowException() throws Exception {
        if (!file.exists() || file.isDirectory()) {
            file.createNewFile();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(toFileData());
        }
    }

    public boolean save() {
        try {
            saveThrowException();
            return true;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public abstract byte[] toFileData();
}
