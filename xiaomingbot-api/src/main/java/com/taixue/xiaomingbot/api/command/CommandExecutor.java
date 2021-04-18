package com.taixue.xiaomingbot.api.command;

import com.taixue.xiaomingbot.api.base.XiaomingPluginObject;
import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.util.ArgumentUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public abstract class CommandExecutor extends XiaomingPluginObject {
    public static class SubcommandExecutor {
        public static class Format {
            Pattern pattern;
            Set<String> variableNames;

            public Format(String format) {
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
                                    patternBuilder.append("(?<" + variableName + ">.*)");
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
                                        patternBuilder.append("(?<" + variableName + ">.*)");
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

            public Pattern getPattern() {
                return pattern;
            }

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
    }

    private List<SubcommandExecutor> subcommandExecutors = new ArrayList<>();

    public void reloadSubcommandExecutor() {
        subcommandExecutors.clear();
        for (Method method : getClass().getMethods()) {
            if (method.getAnnotationsByType(CommandFormat.class).length != 0) {
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

    public boolean verifyPermissionAndReport(CommandSender sender, String node) {
        if (!sender.hasPermission(node)) {
            tellLackPermission(sender, node);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return this.getPlugin().getXiaomingBot();
    }

    public void tellLackPermission(CommandSender sender, String node) {
        sender.sendWarn("小明不能帮你做这件事哦，因为你缺少权限：{} ( ´･･)ﾉ(._.`)", node);
    }

    public String getCommandPrefix() {
        return "#";
    }

    public boolean onCommand(CommandSender sender, String input) {
        final String commandPrefix = getCommandPrefix();
        if (input.startsWith(commandPrefix) && input.length() > commandPrefix.length()) {
            input = input.substring(commandPrefix.length()).trim();
        }
        else {
            return false;
        }

        Class<? extends CommandExecutor> aClass = getClass();
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
                if (!sender.hasPermission(requiredPermission.value())) {
                    tellLackPermission(sender, requiredPermission.value());
                    return true;
                }
            }

            List<Object> arguments = new ArrayList<>();

            // 填充函数参数
            for (Parameter parameter : method.getParameters()) {
                // 如果是带有注解的，必须是 String 类型
                if (parameter.isAnnotationPresent(CommandParameter.class) && parameter.getType().equals(String.class)) {
                    String paraName = parameter.getAnnotation(CommandParameter.class).value();
                    if (matchableFormat.variableNames.contains(paraName)) {
                        arguments.add(matchableMatcher.group(paraName));
                    } else {
                        arguments.add(parameter.getAnnotation(CommandParameter.class).defaultValue());
                    }
                } else if (GroupCommandSender.class.isAssignableFrom(parameter.getType())) {
                    if (sender instanceof GroupCommandSender) {
                        arguments.add(sender);
                    }
                    else {
                        break;
                    }
                } else if (PrivateCommandSender.class.isAssignableFrom(parameter.getType())) {
                    if (sender instanceof PrivateCommandSender) {
                        arguments.add(sender);
                    }
                    else {
                        break;
                    }
                } else if (ConsoleCommandSender.class.isAssignableFrom(parameter.getType())) {
                    if (sender instanceof ConsoleCommandSender) {
                        arguments.add(sender);
                    }
                    else {
                        break;
                    }
                }
                else if (CommandSender.class.isAssignableFrom(parameter.getType())) {
                    arguments.add(sender);
                }
                else {
                    throw new IllegalArgumentException("错误的参数：" + parameter + "，" +
                            "其必须是带 @CommandParameter 注解的 String 类型，" +
                            "或 CommandSender 的子类");
                }
            }

            if (arguments.size() == method.getParameterCount()) {
                getLogger().info("{} 执行指令：{}", sender.getName(), input);
                try {
                    method.invoke(this, arguments.toArray(new Object[0]));
                } catch (Exception exception) {
                    sender.sendError("呜呜呜我遇到了一些错误，我是傻逼 (；′⌒`)");
                    exception.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    @Nullable
    private Map<String, String> parseArguments(String format, String input) {
        int left = format.indexOf("{"), right = 0;
        Map<String, String> result = new HashMap<>();

        List<String> formatArgs = ArgumentUtil.splitArgs(format);
        List<String> inputArgs = ArgumentUtil.splitArgs(input);

        int formatIndex = 0, inputIndex = 0;

        while (formatIndex < formatArgs.size() && inputIndex < inputArgs.size()) {
            String formatWord = formatArgs.get(formatIndex);
            String inputWord = inputArgs.get(inputIndex);

            if (formatWord.startsWith("{")) {
                if (formatWord.equals("{}")) {
                    throw new IllegalArgumentException("变量名不能为空：" + format);
                }
                String variableName = formatWord.substring(1, formatWord.length() - 1).trim();
                if (variableName.isEmpty()) {
                    throw new IllegalArgumentException("变量名不能为空：" + format);
                }
                result.put(variableName, inputWord);
            } else if (!inputWord.matches(formatWord)) {
                return null;
            }

            inputIndex++;
            formatIndex++;
        }

        if (formatIndex < formatArgs.size()) {
            if (formatIndex == formatArgs.size() - 1 &&
                    formatArgs.get(formatArgs.size() - 1).equals("{remain}")) {
                result.put("remain", "");
                return result;
            } else {
                return null;
            }
        }

        if (inputIndex < inputArgs.size()) {
            if (formatArgs.isEmpty()) {
                return null;
            } else if (formatArgs.get(formatArgs.size() - 1).equals("{remain}")) {
                result.put("remain", ArgumentUtil.getReaminArgs(inputArgs, inputIndex - 1));
            } else {
                return null;
            }
        }

        return result;
    }
}
