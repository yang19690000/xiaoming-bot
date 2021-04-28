package com.taixue.xiaoming.bot.host.listener;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.limit.*;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.group.GroupManager;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.core.group.GroupImpl;
import com.taixue.xiaoming.bot.api.listener.dispatcher.Dispatcher;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.GroupDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.PrivateDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.core.listener.dispatcher.DispatcherImpl;
import com.taixue.xiaoming.bot.core.listener.dispatcher.user.GroupDispatcherUserImpl;
import com.taixue.xiaoming.bot.core.listener.dispatcher.user.PrivateDispatcherUserImpl;
import com.taixue.xiaoming.bot.util.TimeUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.FilterValue;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Beans
public class MessageListener extends HostObjectImpl {
    private Map<Long, DispatcherUser> islocater = new HashMap<>();

    final UserCallLimitManager callLimitManager = getXiaomingBot().getUserCallLimitManager();

    final CallLimiter<Long, UserCallRecord> groupCallLimiter = callLimitManager.getGroupCallLimiter();
    final CallLimiter<Long, UserCallRecord> privateCallLimiter = callLimitManager.getPrivateCallLimiter();

    private Dispatcher<PrivateDispatcherUser> privateDispatcher = new DispatcherImpl<PrivateDispatcherUser>() {
        @Override
        public void onInteractorNotFound(@NotNull PrivateDispatcherUser user) {
            user.sendError("小明不知道你的意思");
        }
    };

    private Dispatcher<GroupDispatcherUser> groupDispatcher = new DispatcherImpl<>();

    @OnPrivate
    public void onPrivateMessage(PrivateMsg privateMsg, MsgSender msgSender) {
        final long qq = privateMsg.getAccountInfo().getAccountCodeNumber();

        if (privateCallLimiter.isTooManySoUncallable(qq) && privateCallLimiter.shouldNotice(qq)) {
            final CallLimitConfig config = privateCallLimiter.getConfig();
            final CallRecord userCallRecords = privateCallLimiter.getOrPutCallRecords(qq);

            msgSender.SENDER.sendPrivateMsg(qq, "你" + TimeUtil.toTimeString(config.getPeriod()) + "内已经在群里召唤了" + config.getMaxCallNumber() + "次小明，" +
                    "好好休息一下吧 " + getXiaomingBot().getEmojiManager().get("happy") + "（依旧可以私聊找我哦），" +
                    TimeUtil.after(userCallRecords.getEarlyestRecord(), config.getDeltaNoticeTime()) + "就可以继续在群里召唤我啦");
            privateCallLimiter.setNoticed(qq);
            callLimitManager.save();
        }
        if (privateCallLimiter.uncallable(qq)) {
            return;
        }

        DispatcherUser user = islocater.get(qq);
        final Interactor interactor = Objects.nonNull(user) ? user.getInteractor() : null;
        if (Objects.isNull(user) || user instanceof GroupDispatcherUser) {
            user = new PrivateDispatcherUserImpl();
            user.setInteractor(interactor);
            islocater.put(qq, user);
        }

        final PrivateDispatcherUser privateDispatcherUser = (PrivateDispatcherUser) user;
        privateDispatcherUser.setMsgSender(msgSender);
        privateDispatcherUser.setPrivateMsg(privateMsg);
        if (privateDispatcher.onMessage(privateDispatcherUser)) {
            getXiaomingBot().getConfig().increaseCallCounter();

            // 保存本次输入
            final Account account = privateDispatcherUser.getOrPutAccount();
            account.addPrivateMessage(privateDispatcherUser.getMessage());
            account.save();

            // 保存本次调用
            privateCallLimiter.addCallRecord(qq);
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
        if (groupCallLimiter.isTooManySoUncallable(qq) && groupCallLimiter.shouldNotice(qq)) {
            final CallLimitConfig config = groupCallLimiter.getConfig();
            final CallRecord userCallRecords = groupCallLimiter.getOrPutCallRecords(qq);

            msgSender.SENDER.sendPrivateMsg(qq, "你" + TimeUtil.toTimeString(config.getPeriod()) + "内已经在群里召唤了" + config.getMaxCallNumber() + "次小明，" +
                    "好好休息一下吧 " + getXiaomingBot().getEmojiManager().get("happy") + "（依旧可以私聊找我哦），" +
                    TimeUtil.after(userCallRecords.getEarlyestRecord(), config.getDeltaNoticeTime()) + "就可以继续在群里召唤我啦");

            try {
                msgSender.GETTER.getFriendInfo(qq);
            } catch (Exception exception) {
                msgSender.SENDER.sendPrivateMsg(qq, "只有添加我为好友（马上会通过哦）后小明才会查看你的私聊消息哦，赶快添加小明一起击剑吧 " + getXiaomingBot().getEmojiManager().get("happy"));
            }

            groupCallLimiter.setNoticed(qq);
            callLimitManager.save();
        }
        if (groupCallLimiter.uncallable(qq)) {
            return;
        }



        DispatcherUser user = islocater.get(qq);
        final Interactor interactor = Objects.nonNull(user) ? user.getInteractor() : null;
        if (Objects.isNull(user) || user instanceof PrivateDispatcherUser) {
            user = new GroupDispatcherUserImpl();
            user.setInteractor(interactor);
            islocater.put(qq, user);
        }

        final GroupDispatcherUser groupDispatcherUser = (GroupDispatcherUser) user;
        groupDispatcherUser.setMsgSender(msgSender);
        groupDispatcherUser.setGroupMsg(groupMsg);

        if (groupDispatcher.onMessage(groupDispatcherUser)) {
            getXiaomingBot().getConfig().increaseCallCounter();

            // 保存本次输入
            final Account account = groupDispatcherUser.getOrPutAccount();
            account.addGroupMessage(groupDispatcherUser.getGroup(), groupDispatcherUser.getMessage());
            account.save();

            // 保存本次调用
            groupCallLimiter.addCallRecord(qq);
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
                groups.put(key, new GroupImpl(groupMsg.getGroupInfo()));
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
                    final Group value = new GroupImpl(msgSender.GETTER.getGroupInfo(groupString));
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
