/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.hud;

import net.minecraft.client.gui.GuiGraphics;
import space.hack.Hack;
import space.manager.HackManager;
import space.utils.FontRenderer;
import space.value.HaCd;

import java.awt.*;

public class Hud extends HaCd {
    public boolean dragging;
    public int x = 0, y = 0;
    public int ux = -1, uy = -1;

    public Hud(final String name) {
        super(name);
        this.dragging = false;
    }

    public void upPosOld() {
        this.x = 0;
        this.y = 0;
        this.ux = -1;
        this.uy = -1;
    }

    public FontRenderer drawString(final GuiGraphics graphics, final String text, final int size, final double x, final double y, final Color color) {
        FontRenderer fontRenderer = new FontRenderer(size);
        fontRenderer.drawString(graphics.pose(), text, x, y, color);
        return fontRenderer;
    }

    public FontRenderer drawString(final GuiGraphics graphics, final String text, final double x, final double y, final Color color) {
        return this.drawString(graphics, text, 10, x, y, color);
    }

    @Override
    public String isCategory() {
        return "Hud";
    }

    public boolean isToggled() {
        final Hack hack = HackManager.getHackE("Hud");
        if (hack != null) {
            return hack.isBooleanValue(name);
        }
        return false;
    }

    @Override
    public void toggle() {
        final Hack hack = HackManager.getHackE("Hud");
        if (hack != null) {
            hack.inBooleanValue(name);
        }
    }

    public void isToggled(final boolean toggled) {
        final Hack hack = HackManager.getHackE("Hud");
        if (hack != null) {
            hack.inBooleanValue(name, toggled);
        }
    }

    public void onRender(final GuiGraphics poseStack) {
    }

    public void onRender(final GuiGraphics poseStack, final boolean debug) {
    }

    public void onDebug(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        if (this.dragging) {
            this.x = mouseX;
            this.y = mouseY;
        }
        this.onRender(graphics, true);
    }

}