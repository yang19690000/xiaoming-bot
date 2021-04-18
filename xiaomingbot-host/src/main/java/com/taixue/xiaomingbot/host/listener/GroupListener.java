package com.taixue.xiaomingbot.host.listener;

import com.taixue.xiaomingbot.api.command.CommandFormat;
import com.taixue.xiaomingbot.api.command.RequiredPermission;
import com.taixue.xiaomingbot.api.group.Group;
import com.taixue.xiaomingbot.api.group.GroupManager;
import com.taixue.xiaomingbot.api.listener.userdata.GroupDispatcherUser;
import com.taixue.xiaomingbot.host.command.sender.GroupCommandSender;
import com.taixue.xiaomingbot.host.listener.dispatcher.GroupDispatcher;
import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.util.DateUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.FilterValue;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
@Beans
public class GroupListener extends GroupDispatcher<GroupDispatcherUser> {
    @Override
    public boolean parseCommand(GroupDispatcherUser userData) {
        return XiaomingBot.getInstance().getCommandManager().onCommand(
                new GroupCommandSender(userData.getGroupMsg(), userData.getMsgSender()), userData.getMessage());
    }

    @Override
    public void onNullProcessor(GroupDispatcherUser userData) {}

    @Override
    public GroupDispatcherUser newUserData() {
        return new GroupDispatcherUser();
    }

    @Override
    public GroupInteractor getInteractor(GroupDispatcherUser userData) {
        return XiaomingBot.getInstance().getGroupInteractorManager().getInteractor(userData);
    }

    @OnGroup
    @Override
    public void onGroupMessage(GroupMsg groupMsg, MsgSender msgSender) {
        long group = groupMsg.getGroupInfo().getGroupCodeNumber();
        if (XiaomingBot.getInstance().getGroupManager().containsGroup(group)) {
            getLogger().info("[" + DateUtil.format.format(System.currentTimeMillis()) + "] " +
                    groupMsg.getGroupInfo().getGroupName() +
                    " <" + groupMsg.getGroupInfo().getGroupCodeNumber() + ">" + " " +
                    groupMsg.getAccountInfo().getAccountRemarkOrNickname() +
                    "（" + groupMsg.getAccountInfo().getAccountCodeNumber() +"）\t" + groupMsg.getMsg());
            super.onGroupMessage(groupMsg, msgSender);
        }
    }

    @OnGroup
    @Filter(value = "\\*(开启|启用)小明\\s+{{name}}", matchType = MatchType.REGEX_MATCHES)
    public void onAddGroupResponse(GroupMsg groupMsg,
                                   MsgSender msgSender,
                                   @FilterValue("name") String name) {
        final GroupCommandSender sender = new GroupCommandSender(groupMsg, msgSender);
        final GroupManager groupManager = XiaomingBot.getInstance().getGroupManager();

        final String permissionNode = "response.add";
        if (sender.hasPermission(permissionNode)) {
            if (groupManager.containsGroup(sender.getGroupCode())) {
                sender.sendError("本群已经是小明的响应群了哦");
            }
            else if (groupManager.hasGroup(name)) {
                sender.sendError("已经存在响应群 {} 了，换个名字吧", name);
            }
            else {
                Group group = new Group();
                group.setCode(sender.getGroupCode());
                group.setAlias(sender.getGroupInfo().getGroupName());
                groupManager.addGroup(name, group);
                groupManager.save();
                sender.sendMessage("成功将本群设置为响应群：{}", name);
            }
        }
        else {
            sender.sendError("小明不能帮你做这件事哦，因为你缺少权限{}", permissionNode);
        }
    }
}
