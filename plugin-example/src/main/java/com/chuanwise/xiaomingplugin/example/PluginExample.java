package com.chuanwise.xiaomingplugin.example;

import com.chuanwise.xiaomingplugin.example.interactor.ExampleGroupInteractor;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Chuanwise
 */
public class PluginExample extends XiaomingPlugin {
    @Override
    public void onEnable() {
        getXiaomingBot().getGroupInteractorManager().register(new ExampleGroupInteractor(), this);
    }
}
