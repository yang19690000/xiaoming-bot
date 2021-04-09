package com.taixue.xiaomingbot.util;

import catcode.CatCodeUtil;

import java.io.File;

public class Emojis {
    protected final File directory;

    public Emojis(File directory) {
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
