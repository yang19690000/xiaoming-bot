package com.taixue.xiaoming.bot.api.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequiredPermission;
import com.taixue.xiaoming.bot.api.base.PluginObject;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.GroupDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.PrivateDispatcherUser;
import com.taixue.xiaoming.bot.api.user.*;
import com.taixue.xiaoming.bot.util.ArgumentUtil;
import com.taixue.xiaoming.bot.util.AtUtil;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import kotlinx.coroutines.TimeoutCancellationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public abstract class CommandExecutor extends PluginObject {
    public static class SubcommandExecutor {
        public static class Format {
            Pattern pattern;
            Set<String> variableNames;

            public Format(@NotNull final String format) {
                Pattern result = null;
                StringBuilder patternBuilder = new StringBuilder();

                StringBuilder variableNameBuilder = new StringBuilder();
                StringBuilder variableRegexBuilder = new StringBuilder();
                Set<String> variableNames = new HashSet<>();

                int state = 0;
                char ch;
                boolean spaceChar;
                boolean currentSpaceReplaced = false;

                for (int index = 0; index < format.length(); index++) {
                    ch = format.charAt(index);
                    spaceChar = Character.isSpaceChar(ch);

                    switch (state) {
                        case 0:
                            if (ch == '{') {
                                currentSpaceReplaced = false;
                                state = 1;
                                continue;
                            }
                            if (spaceChar) {
                                if (!currentSpaceReplaced) {
                                    patternBuilder.append("\\s+");
                                    currentSpaceReplaced = true;
                                }
                            } else {
                                patternBuilder.append(ch);
                                currentSpaceReplaced = false;
                            }
                            break;
                        case 1:
                            // 获得变量名
                            switch (ch) {
                                case '}':
                                    // 变量定义终结
                                    final String variableName = variableNameBuilder.toString();
                                    variableNames.add(variableName);
                                    if (Objects.equals(variableName, "remain")) {
                                        patternBuilder.append("(?<remain>.*)");
                                    } else {
                                        patternBuilder.append("(?<" + variableName + ">\\S+)");
                                    }
                                    variableNameBuilder.setLength(0);
                                    state = 0;
                                    break;
                                case ',':
                                    // 正则表达式声明
                                    state = 2;
                                    break;
                                default:
                                    variableNameBuilder.append(ch);
                            }
                            break;
                        case 2:
                            // 获得变量的正则
                            switch (ch) {
                                case '}':
                                    // 变量定义终结
                                    final String variableName = variableNameBuilder.toString();
                                    final String variableRegex = variableRegexBuilder.toString();
                                    variableNames.add(variableName);

                                    if (variableRegex.isEmpty()) {
                                        patternBuilder.append("(?<" + variableName + ">\\S+)");
                                    } else {
                                        patternBuilder.append("(?<" + variableName + ">" + variableRegex + ")");
                                        variableRegexBuilder.setLength(0);
                                    }
                                    variableNameBuilder.setLength(0);
                                    state = 0;
                                    break;
                                default:
                                    variableRegexBuilder.append(ch);
                            }
                            break;
                    }
                }
                this.pattern = Pattern.compile(patternBuilder.toString());
                this.variableNames = variableNames;
            }

            @NotNull
            public Pattern getPattern() {
                return pattern;
            }

            @NotNull
            public Set<String> getVariableNames() {
                return variableNames;
            }
        }

        private Method method;
        private Format[] formats;
        private String[] requiredPermission;

        public SubcommandExecutor(final Method method) {
            this.method = method;
            final CommandFormat[] commandFormats = method.getAnnotationsByType(CommandFormat.class);

            // 记录匹配方式
            List<Format> formats = new ArrayList<>();
            for (CommandFormat commandFormat : commandFormats) {
                formats.add(new Format(commandFormat.value()));
            }
            this.formats = formats.toArray(new Format[0]);

            final RequiredPermission[] permissions = method.getAnnotationsByType(RequiredPermission.class);
            List<String> requiredPermissions = new ArrayList<>();
            for (RequiredPermission permission : permissions) {
                requiredPermissions.add(permission.value());
            }
            this.requiredPermission = requiredPermissions.toArray(new String[0]);
        }

        public SubcommandExecutor(final Method method,
                                  final Format[] formats,
                                  final String[] requiredPermission) {
            this.method = method;
            this.formats = formats;
            this.requiredPermission = requiredPermission;
        }
    }

    private Set<SubcommandExecutor> subcommandExecutors = new HashSet<>();

    public void reloadSubcommandExecutor() {
        subcommandExecutors.clear();
        final String helpPrefix = getHelpPrefix();
        if (!helpPrefix.isEmpty()) {
            final SubcommandExecutor.Format[] formats = new SubcommandExecutor.Format[1];
            formats[0] = new SubcommandExecutor.Format(helpPrefix + " " + CommandWordUtil.HELP_REGEX);
            try {
                SubcommandExecutor helpExecutor = new SubcommandExecutor(getClass().getMethod("onHelp", XiaomingUser.class),
                        formats,
                        new String[0]);
                subcommandExecutors.add(helpExecutor);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        for (Method method : getClass().getMethods()) {
            final CommandFormat[] commandFormats = method.getAnnotationsByType(CommandFormat.class);
            if (commandFormats.length != 0) {
                try {
                    subcommandExecutors.add(new SubcommandExecutor(method));
                } catch (Exception exception) {
                    getLogger().error("方法 {} 不能作为子指令函数，因为解析时出现异常：{}", method, exception);
                    exception.printStackTrace();
                }
            }
        }
        if (subcommandExecutors.isEmpty()) {
            getLogger().warn("没有从加载任何子指令函数");
        } else {
            getLogger().info("成功加载了 {} 个子指令函数", subcommandExecutors.size());
        }
    }

    public boolean verifyPermissionAndReport(@NotNull final XiaomingUser sender,
                                             @NotNull final String node) {
        if (!sender.hasPermission(node)) {
            tellLackPermission(sender, node);
            return false;
        } else {
            return true;
        }
    }

    public void tellLackPermission(@NotNull final XiaomingUser sender,
                                   @NotNull final String node) {
        sender.sendWarning("小明不能帮你做这件事哦，因为你缺少权限：{}", node);
    }

    @NotNull
    public String getCommandPrefix() {
        return "#";
    }

    public boolean onCommand(@NotNull final DispatcherUser user) throws Exception {
        String input = user.getMessage().trim();
        final String commandPrefix = getCommandPrefix();
        if (input.startsWith(commandPrefix) && input.length() > commandPrefix.length()) {
            input = input.substring(commandPrefix.length()).trim();
        } else {
            return false;
        }
        for (SubcommandExecutor subcommandExecutor : subcommandExecutors) {
            SubcommandExecutor.Format matchableFormat = null;
            Matcher matchableMatcher = null;

            for (SubcommandExecutor.Format format : subcommandExecutor.formats) {
                matchableMatcher = format.pattern.matcher(input);
                if (matchableMatcher.matches()) {
                    matchableFormat = format;
                    break;
                }
            }

            // 匹配不成功
            if (Objects.isNull(matchableFormat)) {
                continue;
            }

            final Method method = subcommandExecutor.method;

            // 验证是否具有权限
            for (RequiredPermission requiredPermission : method.getAnnotationsByType(RequiredPermission.class)) {
                if (!user.hasPermission(requiredPermission.value())) {
                    tellLackPermission(user, requiredPermission.value());
                    return true;
                }
            }

            List<Object> arguments = new ArrayList<>();

            // 填充函数参数
            for (Parameter parameter : method.getParameters()) {
                final Class<?> type = parameter.getType();
                // 如果是带有注解的
                if (parameter.isAnnotationPresent(CommandParameter.class)) {
                    final CommandParameter commandParameter = parameter.getAnnotation(CommandParameter.class);
                    final String paraName = commandParameter.value();
                    String paraValue;
                    if (matchableFormat.variableNames.contains(paraName)) {
                        paraValue = matchableMatcher.group(paraName);
                    } else {
                        paraValue = commandParameter.defaultValue();
                    }

                    if (type.equals(String.class)) {
                        // String 类型参数
                        arguments.add(paraValue);
                    } else {
                        final Object o = onParameter(user, type, paraName, paraValue);
                        if (Objects.nonNull(o)) {
                            arguments.add(o);
                        } else {
                            break;
                        }
                    }
                } else if (GroupXiaomingUser.class.isAssignableFrom(type)) {
                    if (user instanceof GroupDispatcherUser) {
                        arguments.add(user);
                    } else {
                        break;
                    }
                } else if (PrivateXiaomingUser.class.isAssignableFrom(type)) {
                    if (user instanceof PrivateDispatcherUser) {
                        arguments.add(user);
                    } else {
                        break;
                    }
                } else if (XiaomingUser.class.isAssignableFrom(type)) {
                    arguments.add(user);
                } else {
                    final Object currentParameter = onParameter(parameter);
                    if (Objects.nonNull(currentParameter)) {
                        arguments.add(currentParameter);
                    } else {
                        throw new IllegalArgumentException("错误的参数：" + parameter);
                    }
                }
            }

            if (arguments.size() == method.getParameterCount()) {
                getLogger().info("{} 执行指令：{}", user.getName(), getCommandPrefix() + input);
                method.invoke(this, arguments.toArray(new Object[0]));
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Object onParameter(@NotNull Parameter parameter) {
        return null;
    }

    @Nullable
    public Object onParameter(@NotNull final DispatcherUser user,
                              @NotNull final Class<?> clazz,
                              @NotNull final String parameterName,
                              @Nullable final String value) {
        if (parameterName.equalsIgnoreCase("qq")) {
            final String qqString = value;
            final long qq = AtUtil.parseQQ(qqString);
            if (qq == -1) {
                user.sendError("{}不是一个合理的QQ哦", qqString);
                return null;
            } else {
                return qq;
            }
        }
        return null;
    }

    @NotNull
    public String getHelpPrefix() {
        return "";
    }

    public void onHelp(@NotNull final XiaomingUser user) {
        StringBuilder builder = new StringBuilder("指令格式（").append(subcommandExecutors.size()).append(" 种）");
        for (SubcommandExecutor subcommandExecutor : subcommandExecutors) {
            for (SubcommandExecutor.Format format : subcommandExecutor.formats) {
                builder.append("\n")
                        .append(getCommandPrefix())
                        .append(format.pattern.pattern()
                                .replaceAll(Pattern.quote("\\s+"), " ")
                                .replaceAll(Pattern.quote("(?"), "")
                                .replaceAll(Pattern.quote("\\S+)"), ""));
            }
        }

        if (user instanceof GroupXiaomingUser) {
            ((GroupXiaomingUser) user).sendPrivateMessage(builder.toString());
            user.sendMessage("该指令的帮助文本已经私发给你啦，记得查收哦");
        } else {
            user.sendMessage(builder.toString());
        }
    }
}
