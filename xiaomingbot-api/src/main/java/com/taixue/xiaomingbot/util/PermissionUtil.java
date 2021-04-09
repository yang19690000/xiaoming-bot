package com.taixue.xiaomingbot.util;

public class PermissionUtil {
    public static boolean accessable(String node, String give) {
        if (node.equals(give)) {
            return true;
        }
        if (node.startsWith("-")) {
            return false;
        }
        if (node.endsWith("*") && give.startsWith(node.substring(0, node.lastIndexOf("*")))) {
            return true;
        }
        return false;
    }
}
