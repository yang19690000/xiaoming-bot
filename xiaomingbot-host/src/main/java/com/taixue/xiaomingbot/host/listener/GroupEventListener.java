package com.taixue.xiaomingbot.host.listener;

import com.taixue.xiaomingbot.api.user.User;
import com.taixue.xiaomingbot.api.user.UserEvent;
import com.taixue.xiaomingbot.host.XiaomingBot;
import com.taixue.xiaomingbot.util.DateUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnGroupAddRequest;
import love.forte.simbot.annotation.OnGroupMute;
import love.forte.simbot.api.message.containers.BeOperatorInfo;
import love.forte.simbot.api.message.containers.OperatorInfo;
import love.forte.simbot.api.message.events.GroupAddRequest;
import love.forte.simbot.api.message.events.GroupMute;

@Beans
public class GroupEventListener {
    @OnGroupMute
    public void onGroupMute(GroupMute groupMute) {
        final long groupCodeNumber = groupMute.getGroupInfo().getGroupCodeNumber();
        if (!XiaomingBot.getInstance().getGroupManager().containsGroup(groupCodeNumber)) {
            return;
        }

        final OperatorInfo operatorInfo = groupMute.getOperatorInfo();
        final BeOperatorInfo beOperatorInfo = groupMute.getBeOperatorInfo();
        final long muteTime = groupMute.getMuteTime();

        // 如果不是小明干的，只能记录了
        if (operatorInfo.getOperatorCodeNumber() != groupMute.getBotInfo().getAccountCodeNumber()) {
            if (muteTime != 0) {
                XiaomingBot.getInstance().getUserManager().findOrNewUser(beOperatorInfo.getBeOperatorCode())
                        .addEvent(UserEvent.groupEvent(groupCodeNumber, beOperatorInfo.getBeOperatorCodeNumber(),
                        "被管理员 " + operatorInfo.getOperatorNicknameAndRemark() +
                                "（" + operatorInfo.getOperatorCode() + "）禁言 " + DateUtil.toTimeString(muteTime)));
                XiaomingBot.getInstance().getUserManager().findOrNewUser(operatorInfo.getOperatorCode())
                        .addEvent(UserEvent.groupEvent(groupCodeNumber, beOperatorInfo.getBeOperatorCodeNumber(),
                                "禁言用户 " + beOperatorInfo.getBeOperatorNicknameAndRemark() +
                                        "（" + beOperatorInfo.getBeOperatorCode() + "）" + DateUtil.toTimeString(muteTime)));
            }
            else {
                XiaomingBot.getInstance().getUserManager().findOrNewUser(beOperatorInfo.getBeOperatorCode())
                        .addEvent(UserEvent.groupEvent(groupCodeNumber, beOperatorInfo.getBeOperatorCodeNumber(),
                                "被管理员 " + operatorInfo.getOperatorNicknameAndRemark() +
                                        "（" + operatorInfo.getOperatorCode() + "）解禁"));
                XiaomingBot.getInstance().getUserManager().findOrNewUser(operatorInfo.getOperatorCode())
                        .addEvent(UserEvent.groupEvent(groupCodeNumber, beOperatorInfo.getBeOperatorCodeNumber(),
                                "解禁用户 " + beOperatorInfo.getBeOperatorNicknameAndRemark() +
                                        "（" + beOperatorInfo.getBeOperatorCode() + "）"));
            }
        }
    }

    @OnGroupAddRequest
    public void onGroupAddRequest(GroupAddRequest groupAddRequest) {
        System.out.println(groupAddRequest);
    }
}
