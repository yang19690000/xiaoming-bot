package com.taixue.xiaomingbot.api.plugin;

/**
 * @author Chuanwise
 */
public class HookHolder {
    protected final XiaomingPlugin sponsor;
    protected final XiaomingPlugin recipient;

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
}
