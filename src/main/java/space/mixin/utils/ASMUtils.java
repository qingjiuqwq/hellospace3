/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有。
 */
package space.mixin.utils;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

public class ASMUtils {

    public static ClassNode node(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            ClassReader reader = new ClassReader(bytes);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            return node;
        }
        return null;
    }

    public static byte[] rewriteClass(@NotNull ClassNode node) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
            @Override
            protected @NotNull String getCommonSuperClass(@NotNull String type1, @NotNull String type2) {
                try {
                    Class<?> class1 = ASMUtils.getClass(type1);
                    Class<?> class2 = ASMUtils.getClass(type2);
                    if (class1 != null && class2 != null) {
                        if (class1.isAssignableFrom(class2)) {
                            return type1;
                        } else if (class2.isAssignableFrom(class1)) {
                            return type2;
                        } else if (!class1.isInterface() && !class2.isInterface()) {
                            do {
                                class1 = class1.getSuperclass();
                            } while (!class1.isAssignableFrom(class2));
                            return class1.getName().replace('.', '/');
                        }
                    }
                } catch (Throwable e) {
                    e.fillInStackTrace();
                }
                return "java/lang/Object";
            }
        };
        node.accept(writer);
        return writer.toByteArray();
    }

    public static Class<?> getClass(String name) {
        Class<?> clazz = null;
        name = name.replace('/', '.');
        try {
            clazz = Class.forName(name);
        } catch (Throwable e) {
            e.fillInStackTrace();
        }
        return clazz;
    }

    public static @NotNull String @NotNull [] split(@NotNull String str, String splitter) {
        if (!str.contains(splitter)) {
            return new String[]{};
        }
        ArrayList<String> result = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder passed = new StringBuilder();
        for (int i = 0; i < str.length() - (splitter.length() - 1); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < i + splitter.length(); j++) {
                sb.append(str.charAt(j));
            }
            if (sb.toString().equals(splitter)) {
                result.add(stringBuilder.toString());
                passed.append(stringBuilder);
                passed.append(splitter);
                stringBuilder = new StringBuilder();
                i += splitter.length();
            }
            if (i < str.length() - 1) {
                stringBuilder.append(str.charAt(i));
            }
        }
        String last = str.replace(passed.toString(), "");
        if (!last.isEmpty()) {
            result.add(last);
        }
        return result.toArray(new String[0]);
    }
}
