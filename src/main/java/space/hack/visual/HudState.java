/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import space.hack.Hack;
import space.hack.HackCategory;
import space.hack.hud.ClickGUI;
import space.hack.hud.Hud;
import space.manager.HackManager;
import space.utils.Utils;
import space.utils.Wrapper;
import space.value.BooleanValue;

public class HudState extends Hack {

    public HudState() {
        super("Hud", HackCategory.Visual);
        for (Hud mode : HackManager.getHud()) {
            this.addValue(new BooleanValue(mode.name, false));
        }
    }

    @Override
    public void onEnable() {
        this.isToggled(false);
        if (Utils.nullCheck()) {
            Wrapper.mc().setScreen(new ClickGUI());
        }
    }

    @Override
    public void onRender(final PoseStack poseStack) {
        for (Hud mode : HackManager.getHud()) {
            if (mode.isToggled()) {
                mode.onRender(poseStack, false);
            }
        }
    }

}