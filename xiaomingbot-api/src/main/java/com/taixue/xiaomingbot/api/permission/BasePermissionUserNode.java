package com.taixue.xiaomingbot.api.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BasePermissionUserNode {
    public abstract void setGroup(String group);

    public abstract void addPermission(String node);

    public abstract boolean hasPrivatePermission(String node);

    public abstract String getGroup();
}
