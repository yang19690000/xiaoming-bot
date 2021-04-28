package com.taixue.xiaoming.bot.util;

public class AtUtil {
    public static long parseQQ(String atString) {
        String qqString;
        long qq;
        if (atString.contains("[CAT:at,code=")) {
            try {
                qqString = atString.substring(atString.indexOf("=") + 1, atString.indexOf("]"));
                qq = Long.parseLong(qqString);
            }
            catch (Exception exception) {
                qq = -1;
            }
        }
        else {
            try {
                qq = Long.parseLong(atString);
            }
            catch (Exception exception) {
                qq = -1;
            }
        }
        return qq;
    }
}
