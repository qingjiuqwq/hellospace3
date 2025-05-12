/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.mixin.utils;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import space.mixin.mapping.Mapping;
import java.lang.reflect.Method;
import java.util.*;

public class Transformer {

    public static final ArrayList<ASMTransformer> transformers = new ArrayList<>();

    public Transformer() {
        System.out.println("[Space] Transformer loaded");
    }

    public static native void trigger(Class<?> cls);

    public static byte[] transform(Class<?> capturedClass, byte[] itsData) {
        for (ASMTransformer transformer : transformers) {
            if (transformer.getTarget() == capturedClass) {
                ClassNode targetNode = ASMUtils.node(itsData);
                for (Method method : transformer.getClass().getDeclaredMethods()) {
                    method.setAccessible(true);
                    if (method.getParameterCount() != 1) {
                        continue;
                    }
                    ASMTransformer.Inject annotation = method.getAnnotation(ASMTransformer.Inject.class);
                    if (annotation == null) {
                        continue;
                    }
                    String name = Mapping.get(transformer.getTarget(), annotation.method(), annotation.desc());
                    MethodNode node = findTargetMethod(targetNode.methods, name, annotation.desc());
                    try {
                        method.invoke(transformer, node);
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }
                    return ASMUtils.rewriteClass(targetNode);
                }
            }
        }
        return null;
    }

    public static MethodNode findTargetMethod(List<MethodNode> list, String name, String desc) {
        return list.stream().filter(m -> m.name.equals(name) && m.desc.equals(DescParser.mapDesc(desc))).findFirst().orElse(null);
    }

//    public Set<Map.Entry<String, byte[]>> transform() {
//        Map<String, byte[]> classMap = new HashMap<>();
//
//        for (ASMTransformer transformer : transformers) {
//            if (transformer.getTarget() == null) {
//                System.out.println("Transformer " + transformer.getClass().getName() + " has no target class, skipping.");
//                continue;
//            }
//            String name = transformer.getTarget().getName().replace('/', '.');
//            byte[] bytes = classMap.get(name);
//            ClassNode targetNode;
//            if (bytes == null) {
//                ClassNode node = null;
//                while (node == null) {
//                    try {
//                        bytes = NativeUtils.getClassesBytes(transformer.getTarget());
//                        node = ASMUtils.node(bytes);
//                    } catch (Throwable e) {
//                        e.fillInStackTrace();
//                    }
//                }
//                targetNode = node;
//            } else {
//                targetNode = ASMUtils.node(bytes);
//            }
//            for (Method method : transformer.getClass().getDeclaredMethods()) {
//                method.setAccessible(true);
//                if (method.getParameterCount() != 1) {
//                    continue;
//                }
//                ASMTransformer.Inject annotation = method.getAnnotation(ASMTransformer.Inject.class);
//                if (annotation == null) {
//                    continue;
//                }
//                MethodNode node = findTargetMethod(targetNode.methods, Mapping.get(transformer.getTarget(), annotation.method(), annotation.desc()), annotation.desc());
//                try {
//                    method.invoke(transformer, node);
//                } catch (IllegalAccessException | InvocationTargetException e) {
//                    e.fillInStackTrace();
//                }
//            }
//            byte[] class_bytes = ASMUtils.rewriteClass(targetNode);
//            classMap.put(name, class_bytes);
//        }
//        return classMap.entrySet();
//    }

    public void addTransformer(ASMTransformer transformer) {
        transformers.add(transformer);
    }

}
