package com.taixue.xiaominglexicons.hook;

import com.taixue.xiaomingbot.api.plugin.HookHolder;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaominglexicons.XiaomingLexicons;

/**
 * 插件通信所用类
 */
public class LexiconsHookHolder extends HookHolder {
    /**
     * 构造一个通信所用类
     * @param sponsor Hook 行为的发起插件
     * @param recipient Hook 行为的接受插件，一般是本插件主类类型
     */
    public LexiconsHookHolder(XiaomingPlugin sponsor, XiaomingLexicons recipient) {
        super(sponsor, recipient);
    }

    /**
     * 插件通信所用方法
     */
    public void action() {

    }
}
