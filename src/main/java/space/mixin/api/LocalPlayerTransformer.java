/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.mixin.api;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import space.hack.Hack;
import space.manager.HackManager;
import space.mixin.invoked.MotionEvent;
import space.mixin.mapping.Mapping;
import space.mixin.utils.ASMTransformer;

public class LocalPlayerTransformer extends ASMTransformer {

    public LocalPlayerTransformer() {
        super(LocalPlayer.class);
    }

    public static MotionEvent onMotion(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean post) {
        // System.out.println("A++ " + (post ? "post" : "get"));
        // System.out.println(x + " " + y + " " + z + " " + yaw + " " + pitch + " " + onGround + " " + post);
        MotionEvent event = new MotionEvent(x, y, z, yaw, pitch, onGround, post);
        for (final Hack hack : HackManager.getHack()) {
            hack.onMotion(event);
        }
        return event;
    }

    @Inject(method = "sendPosition", desc = "()V")
    public void sendPosition(MethodNode methodNode) {

        int j = 1;
        InsnList list = new InsnList();
        InsnList postCall = new InsnList();

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getX", "()D"), "()D")); // 混淆后的 "getX" 方法
        postCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        postCall.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getX", "()D"), "()D")); // 混淆后的 "getX" 方法

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getY", "()D"), "()D")); // 混淆后的 "getY" 方法
        postCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        postCall.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getY", "()D"), "()D")); // 混淆后的 "getY" 方法

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getZ", "()D"), "()D")); // 混淆后的 "getZ" 方法
        postCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        postCall.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getZ", "()D"), "()D")); // 混淆后的 "getZ" 方法

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getYRot", "()F"), "()F")); // 混淆后的 "getYRot" 方法
        postCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        postCall.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getYRot", "()F"), "()F")); // 混淆后的 "getYRot" 方法

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getXRot", "()F"), "()F")); // 混淆后的 "getXRot" 方法
        postCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        postCall.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "getXRot", "()F"), "()F")); // 混淆后的 "getXRot" 方法

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "onGround", "()Z"), "()Z")); // 混淆后的 "onGround" 变量名称
        postCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        postCall.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/Entity", Mapping.get(Entity.class, "onGround", "()Z"), "()Z")); // 混淆后的 "onGround" 变量名称

        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(LocalPlayerTransformer.class), "onMotion", "(DDDFFZZ)L" + MotionEvent.class.getName().replace(".", "/") + ";"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 1));

        postCall.add(new InsnNode(Opcodes.ICONST_1));
        postCall.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(LocalPlayerTransformer.class), "onMotion", "(DDDFFZZ)L" + MotionEvent.class.getName().replace(".", "/") + ";"));
        postCall.add(new InsnNode(Opcodes.POP));

        for (int i = 0; i < methodNode.instructions.size(); ++i) {
            AbstractInsnNode node = methodNode.instructions.get(i);
            if (node instanceof VarInsnNode && ((VarInsnNode) node).var >= j) {
                ((VarInsnNode) node).var += j;
            }
            if (!(node instanceof MethodInsnNode nodeMethod)) {
                continue;
            }
            String methodName = nodeMethod.name;
            System.out.println("[Space] onMotion Method " + methodName);
            if (methodName.equals(Mapping.get(Entity.class, "onGround", "()Z"))) {
                replaceFieldAccess(methodNode, node, "onGround", "Z");
            } else if (methodName.equals(Mapping.get(Entity.class, "getYRot", "()F"))) {
                replaceFieldAccess(methodNode, node, "yaw", "F");
            } else if (methodName.equals(Mapping.get(Entity.class, "getXRot", "()F"))) {
                replaceFieldAccess(methodNode, node, "pitch", "F");
            } else if (methodName.equals(Mapping.get(Entity.class, "getX", "()D"))) {
                replaceFieldAccess(methodNode, node, "x", "D");
            } else if (methodName.equals(Mapping.get(Entity.class, "getY", "()D"))) {
                replaceFieldAccess(methodNode, node, "y", "D");
            } else if (methodName.equals(Mapping.get(Entity.class, "getZ", "()D"))) {
                replaceFieldAccess(methodNode, node, "z", "D");
            }
        }
        methodNode.instructions.insert(list);
        methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), postCall);
    }

    private void replaceFieldAccess(MethodNode methodNode, AbstractInsnNode node, String fieldName, String desc) {
        AbstractInsnNode prev = node.getPrevious();
        if (prev instanceof VarInsnNode && prev.getOpcode() == Opcodes.ALOAD) {
            methodNode.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 1));
            methodNode.instructions.remove(prev);
            methodNode.instructions.insertBefore(node, new FieldInsnNode(Opcodes.GETFIELD, MotionEvent.class.getName().replace(".", "/"), fieldName, desc));
            methodNode.instructions.remove(node);
        }
    }

}