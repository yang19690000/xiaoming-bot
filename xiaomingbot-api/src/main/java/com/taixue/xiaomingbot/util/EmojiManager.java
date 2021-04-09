package com.taixue.xiaomingbot.util;

import catcode.CatCodeUtil;

import java.io.File;

public class EmojiManager {
    protected final File directory;

    public EmojiManager(File directory) {
        this.directory = directory;
    }

    public String getEmoji(String emojiName, String extension) {
        try {
            return CatCodeUtil.INSTANCE.getStringTemplate().image(new File(directory, emojiName + "." + extension).getAbsolutePath());
        }
        catch (Exception e) {
            return "(" + emojiName + ")";
        }
    }

    public String getJpgEmoji(String emojiName) {
        return getEmoji(emojiName, "jpg");
    }
}
