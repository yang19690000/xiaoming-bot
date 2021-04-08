package com.taixue.xiaomingbot.util;

import java.util.ArrayList;
import java.util.List;

public class ArgsUtil {
    private ArgsUtil() {}

    public static List<String> splitArgs(String line) {
        line = line.trim();

        ArrayList<String> result = new ArrayList<>();
        StringBuffer current = new StringBuffer();
        boolean isInArgument = false;
        int state = 0;

        for (int index = 0; index < line.length(); index++) {
            char ch = line.charAt(index);
            boolean spaceChar = Character.isSpaceChar(ch);

            switch (state) {
                case 0:
                    if (spaceChar) {
                        continue;
                    }
                    if (current.length() != 0) {
                        result.add(current.toString());
                        current.setLength(0);
                    }
                    if (ch == '\"') {
                        state = 2;
                        continue;
                    }
                    if (ch == '“') {
                        state = 3;
                        continue;
                    }
                    current.append(ch);
                    state = 2;
                    break;
                    // 普通参数内部
                case 2:
                    if (spaceChar) {
                        state = 0;
                    }
                    else {
                        current.append(ch);
                    }
                    break;
                    // 英文引号参数内部
                case 3:
                    if (ch == '\"') {
                        state = 0;
                    }
                    else {
                        current.append(ch);
                    }
                    break;
                    // 中文引号参数内部
                case 4:
                    if (ch == '”') {
                        state = 0;
                    }
                    else {
                        current.append(ch);
                    }
                    break;
            }
        }
        if (current.length() != 0) {
            result.add(current.toString());
            current.setLength(0);
        }
        return result;
    }

    public static String getReaminArgs(List<String> args, int begin) {
        if (args.isEmpty() || begin >= args.size()) {
            return "";
        }
        if (begin == args.size() - 1) {
            return args.get(begin);
        }
        StringBuilder builder = new StringBuilder(args.get(begin));
        for (int index = begin + 1; index < args.size(); index ++) {
            builder.append(" ").append(args.get(index));
        }
        return builder.toString();
    }
}
