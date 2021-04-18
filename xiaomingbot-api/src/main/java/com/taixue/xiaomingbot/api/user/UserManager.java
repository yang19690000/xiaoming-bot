package com.taixue.xiaomingbot.api.user;

import com.taixue.xiaomingbot.util.JSONFileData;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class UserManager extends JSONFileData {
    private Map<String, User> users;

    public static boolean mute(long group, long qq, long time, MsgSender msgSender) {
        try {
            long botQQ = msgSender.GETTER.getBotInfo().getAccountCodeNumber();
            GroupMemberInfo botGroupInfo = msgSender.GETTER.getMemberInfo(group, botQQ);
            GroupMemberInfo muterGroupInfo = msgSender.GETTER.getMemberInfo(group, qq);

            if (botGroupInfo.getPermission().compareTo(muterGroupInfo.getPermission()) < 0) {
                msgSender.SETTER.setGroupBan(group, qq, time);
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    @Nullable
    public User findUser(String qq) {
        return users.get(qq);
    }

    @Nullable
    public User findOrNewUser(String qq) {
        final User user = findUser(qq);
        if (Objects.isNull(user)) {
            return addUser(qq);
        }
        else {
            return user;
        }
    }

    @Nullable
    public User addUser(String qq) {
        User user = User.user(Long.parseLong(qq));
        return addUser(user);
    }

    @Nullable
    public User addUser(User user) {
        users.put(user.getQq() + "", user);
        return user;
    }


}
