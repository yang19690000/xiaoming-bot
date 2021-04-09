package com.taixue.xiaomingbot.api.command;

import com.taixue.xiaomingbot.api.base.XiaomingPluginObject;
import com.taixue.xiaomingbot.api.bot.XiaomingBot;
import com.taixue.xiaomingbot.util.ArgsUtil;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.sender.MsgSender;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public abstract class CommandExecutor extends XiaomingPluginObject {
    public boolean verifyPermissionAndReport(CommandSender sender, String node) {
        if (!sender.hasPermission(node)) {
            tellLackPermission(sender, node);
            return false;
        }
        else {
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

    public boolean onCommand(CommandSender sender, String input) {
        Class<? extends CommandExecutor> aClass = getClass();
        for (Method method : aClass.getMethods()) {
            CommandFormat[] commandFormats = method.getAnnotationsByType(CommandFormat.class);
            if (commandFormats.length != 0) {
                // 匹配当前函数的输入
                Map<String, String> argumentValMap = null;

                for (CommandFormat commandFormat : commandFormats) {
                    argumentValMap = parseArguments(commandFormat.value(), input);
                    if (Objects.nonNull(argumentValMap)) {
                        break;
                    }
                }

                // 找不到匹配的格式
                if (Objects.isNull(argumentValMap)) {
                    continue;
                }

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
                    if (parameter.isAnnotationPresent(CommandParameter.class)) {
                        String paraName = parameter.getAnnotation(CommandParameter.class).value();
                        if (argumentValMap.containsKey(paraName)) {
                            arguments.add(argumentValMap.get(paraName));
                        }
                        else {
                            arguments.add(parameter.getAnnotation(CommandParameter.class).defaultValue());
                        }
                    }
                    else if (parameter.getType().equals(CommandSender.class)) {
                        arguments.add(sender);
                    }
                    else {
                        throw new IllegalArgumentException("不存在的依赖项：" + parameter);
                    }
                }

                try {
                    method.invoke(this, arguments.toArray(new Object[0]));
                }
                catch (Exception exception) {
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

        List<String> formatArgs = ArgsUtil.splitArgs(format);
        List<String> inputArgs = ArgsUtil.splitArgs(input);

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
            }
            else if (!inputWord.matches(formatWord)) {
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
            }
            else {
                return null;
            }
        }

        if (inputIndex < inputArgs.size()) {
            if (formatArgs.isEmpty()) {
                return null;
            }
            else if (formatArgs.get(formatArgs.size() - 1).equals("{remain}")) {
                result.put("remain", ArgsUtil.getReaminArgs(inputArgs, inputIndex - 1));
            }
            else {
                return null;
            }
        }

        return result;
    }
}
