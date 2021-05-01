package com.taixue.xiaoming.bot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtUtil {
    private static final Pattern AT_CATCODE_PATTERN = Pattern.compile("\\[CAT:at,code=(?<qq>\\d+)\\]");

    public static long parseQQ(String catCodeOrQQ) {
        final Matcher matcher = AT_CATCODE_PATTERN.matcher(catCodeOrQQ);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group("qq"));
        } else if (catCodeOrQQ.matches("\\d+")) {
            return Long.parseLong(catCodeOrQQ);
        } else {
            return -1;
        }
    }
}
