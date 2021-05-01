package com.taixue.xiaoming.bot.core.command.executor;

import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutorMethod;
import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutorMethodImpl implements CommandExecutorMethod {
    private Method method;
    private CommandFormatImpl[] formats;
    private String[] requiredPermission;
    private String[] usages;

    public CommandExecutorMethodImpl(final Method method) {
        this.method = method;
        final Command[] commands = method.getAnnotationsByType(Command.class);

        // 记录匹配方式
        List<CommandFormat> formats = new ArrayList<>();
        for (Command command : commands) {
            formats.add(new CommandFormatImpl(command.value()));
        }
        this.formats = formats.toArray(new CommandFormatImpl[0]);
        fillUsages();

        final RequirePermission[] permissions = method.getAnnotationsByType(RequirePermission.class);
        List<String> requiredPermissions = new ArrayList<>();
        for (RequirePermission permission : permissions) {
            requiredPermissions.add(permission.value());
        }
        this.requiredPermission = requiredPermissions.toArray(new String[0]);
    }

    private void fillUsages() {
        List<String> usages = new ArrayList<>();
        for (CommandFormatImpl format : formats) {
            final StringBuilder builder = new StringBuilder(format.pattern.pattern()
                    .replaceAll(Pattern.quote("\\s+"), "  ")
                    .replaceAll(Pattern.quote("(?"), "")
                    .replaceAll(Pattern.quote(CommandFormat.NORMAL_VARIABLE_REGEX + ")"), "")
                    .replaceAll(Pattern.quote(CommandFormat.REMAIN_VARIABLE_REGEX + ")"), "")
                    .replaceAll(Pattern.quote("\\[CAT:at,code="), "@")
                    .replaceAll(Pattern.quote("\\]"), ""));
            while (true) {
                final Matcher matcher = CommandExecutor.PARAMETER_REGEX.matcher(builder);
                if (matcher.find()) {
                    builder.replace(matcher.start(), matcher.end(), matcher.group("fst"));
                } else {
                    break;
                }
            }
            usages.add(builder.toString());
        }
        this.usages = usages.toArray(new String[0]);
    }

    public CommandExecutorMethodImpl(final Method method,
                                     final CommandFormatImpl[] formats,
                                     final String[] requiredPermission) {
        this.method = method;
        this.formats = formats;
        fillUsages();
        this.requiredPermission = requiredPermission;
    }

    @Override
    public String[] getUsages() {
        return usages;
    }

    @Override
    public CommandFormat[] getFormats() {
        return formats;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String[] getRequiredPermission() {
        return requiredPermission;
    }
}
