package com.taixue.xiaoming.bot.core.command.executor;

import com.taixue.xiaoming.bot.api.command.executor.CommandFormat;
import com.taixue.xiaoming.bot.api.exception.XiaomingRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class CommandFormatImpl implements CommandFormat {
    Pattern pattern;
    Set<String> variableNames;

    public CommandFormatImpl(@NotNull final String format) {
        Pattern result;
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
                                patternBuilder.append("(?<remain>" + REMAIN_VARIABLE_REGEX + ")");
                            } else {
                                patternBuilder.append("(?<" + variableName + ">" + NORMAL_VARIABLE_REGEX + ")");
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
                default:
                    throw new XiaomingRuntimeException("在解析指令格式时出现错误的状态：" + state + "（位于指令处理器 " + getClass().getName() + "）");
            }
        }
        this.pattern = Pattern.compile(patternBuilder.toString());
        this.variableNames = variableNames;
    }

    @Override
    @NotNull
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    @NotNull
    public Set<String> getVariableNames() {
        return variableNames;
    }
}
