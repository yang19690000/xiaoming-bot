package com.taixue.xiaoming.bot.core.user;

import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import org.jetbrains.annotations.NotNull;

/**
 * User 是运行时的小明交互者，不一定会被存储
 * @author Chuanwise
 */
public abstract class XiaomingUserImpl extends HostObjectImpl implements XiaomingUser {
    @Override
    public boolean hasPermissions(@NotNull final String[] nodes) {
        for (String node : nodes) {
            if (!hasPermission(node)) {
                return false;
            }
        }
        return true;
    }
}
