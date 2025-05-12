/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.mixin;

import space.Core;
import space.hack.Hack;
import space.manager.HackManager;
import space.mixin.api.LocalPlayerTransformer;
import space.mixin.utils.ASMTransformer;
import space.mixin.utils.Transformer;
import java.io.File;

public class MixinLoader {

    public static Transformer transformer;
    public static boolean flag = true;
    public static File file = new File("C:\\Space\\" + Core.version + "\\Redefiner.dll");

    public MixinLoader() {
        flag = false;
        if (!file.exists()) {
            HackManager.getHack().removeIf(Hack::isMixin);
            return;
        }
        transformer = new Transformer();
        transformer.addTransformer(new LocalPlayerTransformer());
         System.load("C:\\Space\\" + Core.version + "\\Redefiner.dll");
        System.out.println("[Space] Redefiner loaded");
    }

    public void init() {
        if (!file.exists()) {
            return;
        }
        for (ASMTransformer asmTransformer : Transformer.transformers) {
            Transformer.trigger(asmTransformer.getTarget());
        }
    }

}