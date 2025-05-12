/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有。
 */
package space.mixin.mapping;

import org.objectweb.asm.Type;
import space.Core;

import java.io.File;
import java.io.IOException;

public class Mapping {

    private static IMappingFile mappingFile;

    static {
        try {
            mappingFile = IMappingFile.load(new File("C:\\Space\\" + Core.version + "\\mappings.tsrg"));
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public static boolean isNotObfuscated() {
        try {
            Class.forName("net.minecraft.client.Minecraft").getDeclaredField("instance");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String get(Class<?> clazz, String notObfuscatedName, String docs) {

        if (isNotObfuscated()) {
            return notObfuscatedName;
        }

        if (clazz == null || notObfuscatedName == null) {
            return null;
        }

        String className = Type.getInternalName(clazz);
        IMappingFile.IClass mappingClass = mappingFile.getClass(className);

        if (mappingClass == null) {
            return notObfuscatedName;
        }

        if (docs != null) {
            IMappingFile.IMethod method = mappingClass.getMethod(notObfuscatedName, docs);
            return (method != null && method.getMapped() != null) ? method.getMapped() : notObfuscatedName;
        }

        IMappingFile.IField field = mappingClass.getField(notObfuscatedName);
        return (field != null && field.getMapped() != null) ? field.getMapped() : notObfuscatedName;
    }
}