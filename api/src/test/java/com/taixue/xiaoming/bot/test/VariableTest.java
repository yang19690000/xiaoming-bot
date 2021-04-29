package com.taixue.xiaoming.bot.test;

public class VariableTest {
    public static void main(String[] args) {
        System.getProperties().forEach((e, f) -> {
            System.out.println("e = " + e + ", f = " + f);
        });
    }
}
