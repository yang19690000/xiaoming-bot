package com.taixue.xiaoming.bot.api.plugin;

/**
 * @author Chuanwise
 */
public class HookHolder {
    private final XiaomingPlugin sponsor;
    private final XiaomingPlugin recipient;

    public HookHolder(XiaomingPlugin sponsor, XiaomingPlugin recipient) {
        this.sponsor = sponsor;
        this.recipient = recipient;
    }

    public XiaomingPlugin getSponsor() {
        return sponsor;
    }

    public XiaomingPlugin getRecipient() {
        return recipient;
    }

    public XiaomingPlugin getOtherPlugin(XiaomingPlugin plugin) {
        if (plugin == sponsor) {
            return recipient;
        }
        else if (plugin == recipient) {
            return sponsor;
        }
        else {
            throw new IllegalArgumentException("plugin " + plugin + " doesn't related this hook holder");
        }
    }
}
