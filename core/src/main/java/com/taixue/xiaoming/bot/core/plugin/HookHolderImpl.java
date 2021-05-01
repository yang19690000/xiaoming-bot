package com.taixue.xiaoming.bot.core.plugin;

import com.taixue.xiaoming.bot.api.plugin.HookHolder;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;

/**
 * @author Chuanwise
 */
public class HookHolderImpl implements HookHolder {
    private final XiaomingPlugin sponsor;
    private final XiaomingPlugin recipient;

    public HookHolderImpl(XiaomingPlugin sponsor, XiaomingPlugin recipient) {
        this.sponsor = sponsor;
        this.recipient = recipient;
    }

    @Override public XiaomingPlugin getSponsor() {
        return sponsor;
    }

    @Override public XiaomingPlugin getRecipient() {
        return recipient;
    }

    @Override public XiaomingPlugin getOtherPlugin(XiaomingPlugin plugin) {
        if (plugin == sponsor) {
            return recipient;
        }
        else if (plugin == recipient) {
            return sponsor;
        }
        else {
            throw new IllegalArgumentException("plugin " + plugin + " doesn't related this com.taixue.xiaoming.bot.host.hook holder");
        }
    }
}
