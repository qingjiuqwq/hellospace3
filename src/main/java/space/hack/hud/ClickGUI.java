/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import space.manager.HackManager;

public class ClickGUI extends Screen {

    public ClickGUI() {
        super(Component.nullToEmpty("ClickGui"));
    }

    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground(graphics);
        for (Hud mode : HackManager.getHud()) {
            if (mode.isToggled()) {
                mode.onDebug(graphics, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            Hud hud = findClosestHud(mouseX, mouseY);
            if (hud != null) {
                hud.x = (int) mouseX;
                hud.y = (int) mouseY;
                hud.dragging = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            for (Hud mode : HackManager.getHud()) {
                mode.dragging = false;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public Hud findClosestHud(final double mouseX, final double mouseY) {
        Hud closest = null;
        double closestDistance = -1;

        for (Hud hud : HackManager.getHud()) {
            if (hud.isToggled()) {

                double distance = Math.sqrt(Math.pow(hud.x - mouseX, 2) + Math.pow(hud.y - mouseY, 2));

                if (closestDistance == -1 || distance < closestDistance) {
                    if (distance < 16) {
                        closestDistance = distance;
                        closest = hud;
                    }
                }
            }
        }
        return closest;
    }
}