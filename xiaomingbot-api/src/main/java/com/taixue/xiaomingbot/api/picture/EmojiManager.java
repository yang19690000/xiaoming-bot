package com.taixue.xiaomingbot.api.picture;

import catcode.CatCodeUtil;

import java.io.File;

public class EmojiManager {
    private final File directory;

    public EmojiManager(File directory) {
        this.directory = directory;
    }

    public String getEmoji(String emojiName, String extension) {
        try {
            File file = emojiFile(emojiName, extension);
            if (file.exists() && !file.isDirectory()) {
                return CatCodeUtil.INSTANCE.getStringTemplate().image(file.getAbsolutePath());
            }
            else {
                return "(" + emojiName + ")";
            }
        }
        catch (Exception e) {
            return "(" + emojiName + ")";
        }
    }

    public File emojiFile(String emojiName, String extension) {
        return new File(directory, emojiName + "." + extension);
    }

    public boolean hasEmoji(String emojiName, String extension) {
        File file = emojiFile(emojiName, extension);
        return file.exists() && !file.isDirectory();
    }

    public String getJpgEmoji(String emojiName) {
        return getEmoji(emojiName, "jpg");
    }
}
