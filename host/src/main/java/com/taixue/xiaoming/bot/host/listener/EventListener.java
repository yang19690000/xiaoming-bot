package com.taixue.xiaoming.bot.host.listener;

import com.taixue.xiaoming.bot.api.account.Account;
import com.taixue.xiaoming.bot.api.account.AccountManager;
import com.taixue.xiaoming.bot.core.account.AccountEventImpl;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.util.TimeUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnFriendAddRequest;
import love.forte.simbot.annotation.OnGroupMute;
import love.forte.simbot.api.message.containers.*;
import love.forte.simbot.api.message.events.FriendAddRequest;
import love.forte.simbot.api.message.events.GroupMute;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class EventListener extends HostObjectImpl {
    @OnGroupMute
    public void onGroupMute(GroupMute groupMute, MsgSender msgSender) {
        final long group = groupMute.getGroupInfo().getGroupCodeNumber();
        final AccountManager accountManager = getXiaomingBot().getAccountManager();

        if (!getXiaomingBot().getGroupManager().containsGroup(group)) {
            return;
        }

        final OperatorInfo operatorInfo = groupMute.getOperatorInfo();
        final BeOperatorInfo beOperatorInfo = groupMute.getBeOperatorInfo();
        final long muteTime = groupMute.getMuteTime();

        final Account operatorAccount = accountManager.getOrPutAccount(operatorInfo.getOperatorCodeNumber(), operatorInfo.getOperatorNicknameAndRemark());
        final Account beOperatorAccount = accountManager.getOrPutAccount(beOperatorInfo.getBeOperatorCodeNumber(), beOperatorInfo.getBeOperatorNicknameAndRemark());

        // 如果不是小明干的，只能记录了
        if (operatorInfo.getOperatorCodeNumber() != groupMute.getBotInfo().getAccountCodeNumber()) {
            if (muteTime != 0) {
                beOperatorAccount.addHistory(AccountEventImpl.groupEvent(group,
                        "被管理员 " + operatorInfo.getOperatorNicknameAndRemark() +
                                "（" + operatorInfo.getOperatorCode() + "）禁言 " + TimeUtil.toTimeString(muteTime)));
                operatorAccount.addHistory(AccountEventImpl.groupEvent(group,
                        "禁言用户 " + beOperatorInfo.getBeOperatorNicknameAndRemark() +
                                "（" + beOperatorInfo.getBeOperatorCode() + "）" + TimeUtil.toTimeString(muteTime)));
            }
            else {
                beOperatorAccount.addHistory(AccountEventImpl.groupEvent(group,
                        "被管理员 " + operatorInfo.getOperatorNicknameAndRemark() +
                                "（" + operatorInfo.getOperatorCode() + "）解禁"));
                operatorAccount.addHistory(AccountEventImpl.groupEvent(group,
                        "解禁用户 " + beOperatorInfo.getBeOperatorNicknameAndRemark() +
                                "（" + beOperatorInfo.getBeOperatorCode() + "）"));
            }
            operatorAccount.readySave();
            beOperatorAccount.readySave();
        }
    }

    /*
    @OnGroupAddRequest
    public void onGroupAddRequest(GroupAddRequest groupAddRequest, MsgSender msgSender) {
        System.out.println(groupAddRequest);
        final AccountInfo accountInfo = groupAddRequest.getAccountInfo();
        final BotInfo botInfo = groupAddRequest.getBotInfo();
        final GroupInfo groupInfo = groupAddRequest.getGroupInfo();

        if (accountInfo.getAccountCodeNumber() == botInfo.getAccountCodeNumber()) {
            getLogger().info("小明加入了新的群聊：{}", groupInfo.getGroupCode());
        } else {
            getLogger().info("小明所在的群聊 {} 中加入了新的成员：{}", groupInfo.getGroupCode(), accountInfo.getAccountCodeNumber());
        }

        msgSender.SENDER.sendGroupMsg(groupAddRequest.getGroupInfo(), "新的加群申请：" + groupAddRequest.toString());

        // msgSender.SETTER.acceptGroupAddRequest(groupAddRequest.getFlag());
    }

    @OnPrivate
    public void onPrivate(PrivateMsg privateMsg, MsgSender msgSender) {
        System.out.println(privateMsg);
    }

     */

    @OnFriendAddRequest
    public void onFriendAddRequest(FriendAddRequest friendAddRequest, MsgSender msgSender) {
        msgSender.SETTER.acceptFriendAddRequest(friendAddRequest.getFlag(), true);
        msgSender.SENDER.sendPrivateMsg(friendAddRequest.getAccountInfo(), "从今往后小明会响应你的私聊消息啦" + getXiaomingBot().getEmojiManager().get("happy"));
    }
}
