package com.taixue.xiaoming.bot.core.user;

import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User 是运行时的小明交互者，不一定会被存储
 * @author Chuanwise
 */
public abstract class XiaomingUserImpl extends HostObjectImpl implements XiaomingUser {
    private List<String> recentInputs = new ArrayList<>();
    @Override
    public boolean hasPermissions(@NotNull final String[] nodes) {
        for (String node : nodes) {
            if (!hasPermission(node)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> getRecentInputs() {
        return recentInputs;
    }

    @Override
    public void setRecentInputs(List<String> recentInputs) {
        this.recentInputs = recentInputs;
    }
}
