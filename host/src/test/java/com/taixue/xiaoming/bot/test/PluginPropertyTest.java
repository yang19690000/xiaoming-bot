package com.taixue.xiaoming.bot.test;

import com.taixue.xiaoming.bot.api.plugin.PluginProperty;
import com.taixue.xiaoming.bot.core.plugin.PluginPropertyImpl;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;

public class PluginPropertyTest {
    public static void main(String[] args) {
        PluginProperty property = new PluginPropertyImpl();
        property.put("main", PluginPropertyTest.class.getName());
        System.out.println(JsonSerializerUtil.getInstance().toJsonString(property));
    }
}
