/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有。
 */
package space.mixin.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DescParser {

    public static String mapDesc(String content) {
        if (content.contains("(")) {
            return new LMethodDesc(content).map();
        }
        return new LFieldDesc(content).map();
    }

    public static String[] split(@NotNull String str, String splitter) {
        return ASMUtils.split(str, splitter);
    }

    public static String cutBetween(String content, char start, char end) {
        char[] chars = content.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == start) {
                i++;
                while (chars[i] != end) {
                    builder.append(chars[i]);
                    i++;
                }
                return builder.toString();
            }
        }
        return "";
    }

    public static String[] parseType(String content) {
        char[] chars = content.toCharArray();
        ArrayList<String> types = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == 'L') {
                i++;
                while (chars[i] != ';') {
                    builder.append(chars[i]);
                    i++;
                }
                types.add(builder.toString());
                builder = new StringBuilder();
            }
        }
        return types.toArray(new String[0]);
    }

    public static class LMethodDesc {
        String source;
        String[] paramTypes;
        String returnType;

        public LMethodDesc(String content) {
            source = content;
            paramTypes = parseType(cutBetween(content, '(', ')'));
            String[] returnType = parseType(split(content, ")")[1]);
            this.returnType = returnType.length > 0 ? returnType[0] : "";
        }

        public String map() {
            return source;
        }
    }

    public static class LFieldDesc {
        String source;
        String type;

        public LFieldDesc(String content) {
            source = content;
            String[] values = parseType(content);
            type = values.length > 0 ? values[0] : "";
        }

        public String map() {
            return source;
        }
    }

}
