/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.visual;

import net.minecraft.client.gui.GuiGraphics;
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
            BooleanValue booleanValue = new BooleanValue(mode.name, false);
            booleanValue.setInfo(mode.name);
            this.addValue(booleanValue);
        }
    }

    @Override
    public void onEnable() {
        this.isToggled(false);
        if (Utils.nullCheck()) {
            boolean show = false;
            for (Hud mode : HackManager.getHud()) {
                if (mode.isToggled()) {
                    show = true;
                }
            }
            if (show) {
                Wrapper.mc().setScreen(new ClickGUI());
            }
        }
    }

    @Override
    public void onRender(final GuiGraphics graphics) {
        for (Hud mode : HackManager.getHud()) {
            if (mode.isToggled()) {
                mode.onRender(graphics);
                mode.onRender(graphics, false);
            }
        }
    }

}