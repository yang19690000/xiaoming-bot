package com.taixue.xiaoming.bot.test;

import com.taixue.xiaoming.bot.api.data.JsonFileSavedData;
import com.taixue.xiaoming.bot.api.factory.normal.JsonFileSavedDataFactory;

import java.io.File;

public class JsonTest extends JsonFileSavedData {
    private String qwq = "test";

    public JsonTest(String qwq) {
        this.qwq = qwq;
    }

    public static void main(String[] args) {
        final JsonFileSavedDataFactory factory = new JsonFileSavedDataFactory();
        final JsonTest testValue = factory.forFileOrProduce(new File("jsontest.json"), JsonTest.class, () -> {
            JsonTest test = new JsonTest("sdfsdfsdf");
            return test;
        });
        System.out.println(testValue.qwq);
        testValue.save();
    }
}
