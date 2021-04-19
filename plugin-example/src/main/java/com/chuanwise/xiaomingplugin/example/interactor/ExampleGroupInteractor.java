package com.chuanwise.xiaomingplugin.example.interactor;

import com.taixue.xiaomingbot.api.listener.interactor.GroupInteractor;
import com.taixue.xiaomingbot.api.listener.userdata.GroupInteractorUser;
import com.taixue.xiaomingbot.util.AtUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public class ExampleGroupInteractor extends GroupInteractor<GroupInteractorUser> {
    public static final Pattern TELL_JOKE_TO = Pattern.compile("讲个(冷)?笑话给\\s*(?<qq>.*)\\s*(听)?");
    @Override
    public boolean onMessage(GroupInteractorUser groupInteractorUser) {
        final String message = groupInteractorUser.getMessage();
        if (!message.equals("小明帮帮我")) {
            return false;
        }

        groupInteractorUser.sendMessage("来啦，啥事 qwq？");
        final String nextInput = getNextInput(groupInteractorUser);
        final Matcher matcher = TELL_JOKE_TO.matcher(nextInput);

        if (matcher.matches()) {
            final String qqString = matcher.group("qq");
            final long qq = AtUtil.parseQQ(qqString);

            if (qq == -1) {
                groupInteractorUser.sendMessage("{} 是谁呀 (╯▔皿▔)╯", qqString);
            }
            else {
                groupInteractorUser.atSendGroupMessage(qq, "从前有座山，山上有座庙，庙里有个老和尚在玩 Minecraft。");
            }
        }
        else {
            groupInteractorUser.sendMessage("小明不知道你说的是什么意思呢 (；′⌒`)");
        }
        return true;
    }
}
