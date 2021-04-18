package com.taixue.xiaomingbot.util;

public class CommandWordUtil {
    public static final String GLOBAL_REGEX = "(全局|公共|global)";
    public static final String LEXICONS_REGEX = "(词库|问答|词条|lexicon)";
    public static final String GLOBAL_LEXICONS_REGEX = GLOBAL_REGEX + LEXICONS_REGEX;
    public static final String RELOAD_REGEX = "(重载|重加载|reload)";
    public static final String LOOK_REGEX = "(查看|罗列|list|look)";
    public static final String NEW_REGEX = "(新增|新建|增加|增添|添加|新|add|new)";
    public static final String REMOVE_REGEX = "(删除|删掉|卸载|删|取消|移除|去除|减少|remove|delete)";
    public static final String PERSONAL_REGEX = "(我的|私人|私有|用户|my|personal|private)";
    public static final String PERSONAL_LEXICONS_REGEX = "(" + PERSONAL_REGEX + ")?" + LEXICONS_REGEX;
    public static final String OTHER_REGEX = "(他人|别人|他|别人|his|her|its)";
    public static final String PLUGIN_REGEX = "(插件|plugin|plugins)";
    public static final String PERMISSION_GROUP_REGEX = "(权限|权限组|permission)";
    public static final String ALIAS_REGEX = "(别名|昵称|绰号|备注|alias)";
    public static final String CONFIRM_REGEX = "(确认|confirm)";
    public static final String USER_REGEX = "(玩家|用户|好友|friend)";
    public static final String XIAOMING_REGEX = "((太学)?小明|xiaoming)";
    public static final String STATUS_REGEX = "(状态|status)";
    public static final String HELP_REGEX = "(帮助|说明|help)";
    public static final String GROUP_REGEX = "(群)";
}
