package com.taixue.xiaoming.bot.core.user;

import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author Chuanwise
 */
public abstract class QQXiaomingUserImpl extends XiaomingUserImpl implements QQXiaomingUser {
    @Override
    public boolean hasPermission(String node) {
        return getXiaomingBot().getPermissionManager().userHasPermission(getQQ(), node);
    }

    @Override
    public String getName() {
        return getQQString();
    }
}
