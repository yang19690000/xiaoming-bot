package com.taixue.xiaoming.bot.host.listener;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.base.HostObject;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.api.limit.CallLimitManager;
import com.taixue.xiaoming.bot.api.listener.dispatcher.Dispatcher;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.GroupDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.PrivateDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.util.TimeUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.FilterValue;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.message.results.FriendInfo;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import net.mamoe.mirai.Bot;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Beans
public class MessageListener extends HostObject {
    private Map<Long, DispatcherUser> islocater = new HashMap<>();

    final CallLimitManager callLimitManager = getXiaomingBot().getCallLimitManager();
    final CallLimitManager.Config config = callLimitManager.getConfig();

    private Dispatcher<PrivateDispatcherUser> privateDispatcher = new Dispatcher<PrivateDispatcherUser>() {
        @Override
        public void onInteractorNotFound(@NotNull PrivateDispatcherUser user) {
            user.sendError("小明不知道你的意思");
        }
    };

    private Dispatcher<GroupDispatcherUser> groupDispatcher = new Dispatcher<>();

    @OnPrivate
    public void onPrivateMessage(PrivateMsg privateMsg, MsgSender msgSender) {
        final long qq = privateMsg.getAccountInfo().getAccountCodeNumber();

        final CallLimitManager.UserCallRecords userCallRecords = callLimitManager.getUserCallRecords(qq);
        if (Objects.nonNull(userCallRecords) && userCallRecords.getPrivateRecords().isTooFastSoUncallable(config)) {
            return;
        }

        DispatcherUser user = islocater.get(qq);
        final Interactor interactor = Objects.nonNull(user) ? user.getInteractor() : null;
        if (Objects.isNull(user) || user instanceof GroupDispatcherUser) {
            user = new PrivateDispatcherUser();
            user.setInteractor(interactor);
            islocater.put(qq, user);
        }

        final PrivateDispatcherUser privateDispatcherUser = (PrivateDispatcherUser) user;
        privateDispatcherUser.setMsgSender(msgSender);
        privateDispatcherUser.setPrivateMsg(privateMsg);
        if (privateDispatcher.onMessage(privateDispatcherUser)) {
            getXiaomingBot().getConfig().increaseCallCounter();
            final Account account = privateDispatcherUser.getOrPutAccount();
            account.addPrivateCommandEvent(privateMsg.getMsg());

            callLimitManager.getOrPutUserCallRecords(qq).getPrivateRecords().addNewCall(config);
            callLimitManager.save();
        }
    }

    @OnGroup
    public void onGroupMessage(GroupMsg groupMsg,
                               MsgSender msgSender) {
        final long group = groupMsg.getGroupInfo().getGroupCodeNumber();
        final long qq = groupMsg.getAccountInfo().getAccountCodeNumber();
        if (!isResponseGroup(group)) {
            return;
        }

        // 如果有调用记录，并且很长一段时间没有提醒调用太多次
        final CallLimitManager.UserCallRecords userCallRecords = callLimitManager.getUserCallRecords(qq);
        if (Objects.nonNull(userCallRecords)) {
            // 很长一段时间都没调用，才考虑提醒调用太多次
            final CallLimitManager.UserCallRecord groupRecords = userCallRecords.getGroupRecords();
            if (groupRecords.getLastNoticeTime() + config.getMaxDeltaCallTime() < System.currentTimeMillis()) {
                if (groupRecords.isTooManySoUncallable(config)) {
                    try {
                        msgSender.GETTER.getFriendInfo(qq);
                        msgSender.SENDER.sendPrivateMsg(qq, "你一段时间以内的调用次数已达上限。\n" +
                                "每" + TimeUtil.toTimeString(config.getMaxDeltaCallTime()) +
                                "只能在群里召唤" + config.getMaxCallNumber() + "次小明（私聊则无此限制）。");
                    } catch (Exception exception) {
                        msgSender.SENDER.sendPrivateMsg(qq, "你一段时间以内的调用次数已达上限。\n" +
                                "每" + TimeUtil.toTimeString(config.getMaxDeltaCallTime()) +
                                "只能在群里召唤" + config.getMaxCallNumber() + "次小明（私聊则无此限制）。\n" +
                                "只有添加我为好友（马上会通过哦）后小明才会查看你的私聊消息哦，赶快添加小明一起击剑吧 " +
                                getXiaomingBot().getEmojiManager().get("happy"));
                    }
                    groupRecords.updateLastNoticeTime();
                    callLimitManager.save();
                }
            }

            // 超过调用限制则忽略消息
            if (!groupRecords.callable(config)) {
                return;
            }
        }

        DispatcherUser user = islocater.get(qq);
        final Interactor interactor = Objects.nonNull(user) ? user.getInteractor() : null;
        if (Objects.isNull(user) || user instanceof PrivateDispatcherUser) {
            user = new GroupDispatcherUser();
            user.setInteractor(interactor);
            islocater.put(qq, user);
        }

        final GroupDispatcherUser groupDispatcherUser = (GroupDispatcherUser) user;
        groupDispatcherUser.setMsgSender(msgSender);
        groupDispatcherUser.setGroupMsg(groupMsg);

        if (groupDispatcher.onMessage(groupDispatcherUser)) {
            getXiaomingBot().getConfig().increaseCallCounter();
            final Account account = groupDispatcherUser.getOrPutAccount();
            account.addGroupCommandEvent(group, groupMsg.getMsg());
            account.save();

            // 调用成功后，记录本次调用
            final CallLimitManager.UserCallRecord callRecords = callLimitManager.getOrPutUserCallRecords(qq).getGroupRecords();
            callRecords.addNewCall(config);
            callLimitManager.save();
        }
    }

    public boolean isResponseGroup(final long group) {
        final GroupManager groupManager = getXiaomingBot().getGroupManager();
        if (getXiaomingBot().getConfig().isDebug()) {
            final Set<Group> debug = groupManager.forTag("debug");
            return debug.contains(group);
        } else {
            final Collection<Group> values = groupManager.getGroups().values();
            for (Group value : values) {
                if (value.getCode() == group) {
                    return true;
                }
            }
            return false;
        }
    }

    @OnGroup
    @Filter(value = "\\*启动小明\\s+{{key,\\S+}}", matchType = MatchType.REGEX_MATCHES)
    public void onEnableXiaoming(GroupMsg groupMsg,
                                 MsgSender msgSender,
                                 @FilterValue("key") String key) {
        final GroupManager groupManager = getXiaomingBot().getGroupManager();
        final Map<String, Group> groups = groupManager.getGroups();

        final String permissionNode = "group.add";
        if (getXiaomingBot().getPermissionManager().userHasPermission(groupMsg.getAccountInfo().getAccountCodeNumber(),
                permissionNode)) {
            if (groups.containsKey(key)) {
                msgSender.SENDER.sendGroupMsg(groupMsg, "本群已经是小明的响应群了哦");
            }
            else {
                groups.put(key, new Group(groupMsg.getGroupInfo()));
                msgSender.SENDER.sendGroupMsg(groupMsg, "成功将本群设置为小明响应群：" + key);
                groupManager.save();
            }
        } else {
            msgSender.SENDER.sendGroupMsg(groupMsg, "小明不能帮你做这件事哦，因为你缺少权限：" + permissionNode);
        }
    }

    @OnGroup
    @Filter(value = "\\*启动小明\\s+{{group,\\d+}}\\s+{{key,\\S+}}", matchType = MatchType.REGEX_MATCHES)
    public void onEnableXiaoming(GroupMsg groupMsg,
                                 MsgSender msgSender,
                                 @FilterValue("key") String key,
                                 @FilterValue("group") String groupString) {
        final GroupManager groupManager = getXiaomingBot().getGroupManager();
        final Map<String, Group> groups = groupManager.getGroups();

        final String permissionNode = "group.add";
        if (getXiaomingBot().getPermissionManager().userHasPermission(groupMsg.getAccountInfo().getAccountCodeNumber(),
                permissionNode)) {
            if (groups.containsKey(key)) {
                msgSender.SENDER.sendGroupMsg(groupMsg, key + "已经是小明的响应群了");
            }
            else {
                try {
                    final Group value = new Group(msgSender.GETTER.getGroupInfo(groupString));
                    groups.put(key, value);
                    msgSender.SENDER.sendGroupMsg(groupMsg, "成功添加了新的小明响应群：" + value.getAlias());
                    groupManager.save();
                } catch (Exception exception) {
                    msgSender.SENDER.sendGroupMsg(groupMsg, "无法添加该群为小明的响应群，可能是这个群不存在、小明还不在群里或缓存还未刷新");
                }
            }
        } else {
            msgSender.SENDER.sendGroupMsg(groupMsg, "小明不能帮你做这件事哦，因为你缺少权限：" + permissionNode);
        }
    }
}
