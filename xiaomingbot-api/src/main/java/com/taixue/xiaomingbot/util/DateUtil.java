package com.taixue.xiaomingbot.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateUtil {
    private DateUtil() {}

    public static final DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");

    public static final long SECOND_MINS = 1000;
    public static final long MINUTE_MINS = SECOND_MINS * 60;
    public static final long HOUR_MINS = MINUTE_MINS * 60;
    public static final long DAY_MINS = HOUR_MINS * 24;
    public static final long MOUTH_MINS = DAY_MINS * 30;

    public static String getTimeString(long time) {
        long days = time / DAY_MINS;
        time -= days * DAY_MINS;
        long hours = time / HOUR_MINS;
        time -= hours * HOUR_MINS;
        long minutes = time / MINUTE_MINS;
        time -= minutes * MINUTE_MINS;
        long seconds = time / SECOND_MINS;
        time -= seconds * SECOND_MINS;

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days + "天");
        }
        if (hours > 0) {
            result.append(hours + "小时");
        }
        if (minutes > 0) {
            result.append(minutes + "分");
        }
        if (result.length() == 0 && seconds > 0) {
            result.append(seconds + "秒");
        }
        if (result.length() == 0 && time > 0) {
            result.append(time + "毫秒");
        }
        return result.toString();
    }

    public static long parseTime(String timeString) {
        if (!timeString.matches("(\\d+[Dd天Hh时Mm分Ss秒])+")) {
            return -1;
        }
        long totalTime = 0;
        long currentNumber = 0;
        for (int index = 0; index < timeString.length(); index ++) {
            if (Character.isDigit(timeString.charAt(index))) {
                currentNumber *= 10;
                currentNumber += timeString.charAt(index) - '0';
                continue;
            }
            if ("Dd天".contains("" + timeString.charAt(index))) {
                totalTime += DAY_MINS * currentNumber;
                currentNumber = 0;
                continue;
            }
            if ("Hh时".contains("" + timeString.charAt(index))) {
                totalTime += HOUR_MINS * currentNumber;
                currentNumber = 0;
                continue;
            }
            if ("Mm分".contains("" + timeString.charAt(index))) {
                totalTime += MINUTE_MINS * currentNumber;
                currentNumber = 0;
                continue;
            }
            if ("Ss秒".contains("" + timeString.charAt(index))) {
                totalTime += SECOND_MINS * currentNumber;
                currentNumber = 0;
                continue;
            }
            return -1;
        }
        return totalTime;
    }
}
