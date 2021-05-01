package com.taixue.xiaoming.bot.core.command.executor;

import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutorMethod;
import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.DispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.GroupDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.PrivateDispatcherUser;
import com.taixue.xiaoming.bot.api.user.*;
import com.taixue.xiaoming.bot.core.base.PluginObjectImpl;
import com.taixue.xiaoming.bot.util.AtUtil;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author Chuanwise
 */
public abstract class CommandExecutorImpl extends PluginObjectImpl implements CommandExecutor {
    private Set<CommandExecutorMethod> executorMethods = new HashSet<>();

    @Override
    public void reloadSubcommandExecutor() {
        executorMethods.clear();
        final String helpPrefix = getHelpPrefix();
        if (!helpPrefix.isEmpty()) {
            final CommandFormatImpl[] formats = new CommandFormatImpl[1];
            formats[0] = new CommandFormatImpl(helpPrefix + " " + CommandWordUtil.HELP_REGEX);
            try {
                CommandExecutorMethod helpExecutor = new CommandExecutorMethodImpl(getClass().getMethod("onHelp", XiaomingUser.class),
                        formats,
                        new String[0]);
                executorMethods.add(helpExecutor);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        for (Method method : getClass().getMethods()) {
            final Command[] commands = method.getAnnotationsByType(Command.class);
            if (commands.length != 0) {
                try {
                    executorMethods.add(new CommandExecutorMethodImpl(method));
                } catch (Exception exception) {
                    getLogger().error("方法 {} 不能作为子指令处理方法，因为解析时出现异常：{}", method, exception);
                    exception.printStackTrace();
                }
            }
        }
        if (executorMethods.isEmpty()) {
            getLogger().warn("没有从加载任何子指令处理方法");
        } else {
            getLogger().info("成功加载了 {} 个子指令处理方法", executorMethods.size());
        }
    }

    @Override
    @NotNull
    public String getCommandPrefix() {
        return "#";
    }

    @Override
    public boolean onCommand(@NotNull final DispatcherUser user) throws Exception {
        String input = user.getMessage().trim();
        final String commandPrefix = getCommandPrefix();
        if (input.startsWith(commandPrefix) && input.length() > commandPrefix.length()) {
            input = input.substring(commandPrefix.length()).trim();
        } else {
            return false;
        }
        for (CommandExecutorMethod executorMethod : executorMethods) {
            CommandFormat matchableFormat = null;
            Matcher matchableMatcher = null;

            for (CommandFormat format : executorMethod.getFormats()) {
                matchableMatcher = format.getPattern().matcher(input);
                if (matchableMatcher.matches()) {
                    matchableFormat = format;
                    break;
                }
            }

            // 匹配不成功
            if (Objects.isNull(matchableFormat)) {
                continue;
            }

            final Method method = executorMethod.getMethod();

            // 验证是否具有权限
            for (RequirePermission requiredPermission : method.getAnnotationsByType(RequirePermission.class)) {
                if (!user.checkPermissionAndReport(requiredPermission.value())) {
                    return true;
                }
            }

            List<Object> arguments = new ArrayList<>();

            // 填充处理方法参数
            for (Parameter parameter : method.getParameters()) {
                final Class<?> type = parameter.getType();
                // 如果是带有注解的
                if (parameter.isAnnotationPresent(CommandParameter.class)) {
                    final CommandParameter commandParameter = parameter.getAnnotation(CommandParameter.class);
                    final String paraName = commandParameter.value();
                    String paraValue;
                    if (matchableFormat.getVariableNames().contains(paraName)) {
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
                method.invoke(this, arguments.toArray(new Object[0]));
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public Object onParameter(@NotNull Parameter parameter) {
        return null;
    }

    @Override
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

    @Override
    @NotNull
    public String getHelpPrefix() {
        return "";
    }

    @Override
    public Set<String> getUsageStrings(@NotNull final XiaomingUser user) {
        Set<String> usages = new HashSet<>();
        for (CommandExecutorMethod executorMethod : executorMethods) {
            if (user.hasPermissions(executorMethod.getRequiredPermission())) {
                for (String usage : executorMethod.getUsages()) {
                    usages.add(getCommandPrefix() + usage);
                }
            }
        }
        return usages;
    }

    @Override
    public void onHelp(@NotNull final XiaomingUser user) {
        StringBuilder builder = new StringBuilder();

        final Set<String> usageStrings = getUsageStrings(user);
        if (usageStrings.isEmpty()) {
            builder.append("你没有权限执行该组任何一个指令");
        } else {
            final String[] strings = usageStrings.toArray(new String[0]);
            Arrays.sort(strings);
            builder.append("该组指令中你可能有权执行的有如下 " + usageStrings.size() + " 条：");
            for (String s : strings) {
                builder.append("\n").append(s);
            }
        }

        if (user instanceof GroupXiaomingUser) {
            ((GroupXiaomingUser) user).sendPrivateMessage(builder.toString());
        } else {
            user.sendMessage(builder.toString());
        }
    }
}