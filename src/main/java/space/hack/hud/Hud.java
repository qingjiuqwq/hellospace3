/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import space.hack.Hack;
import space.manager.HackManager;
import space.utils.Wrapper;
import space.value.HaCd;

public class Hud extends HaCd
{
    public boolean dragging;
    public int x = 0, y = 0;
    public int ux = -1, uy = -1;

    public Hud(final String name) {
        super(name);
        this.dragging = false;
    }

    public void upPosOld(){
        this.x = 0;
        this.y = 0;
        this.ux = -1;
        this.uy = -1;
    }

    public void drawString(final PoseStack poseStack, final String text, final float size, final float x, final float y, final int color) {
        float scalingFactor = size / 10.0f;

        poseStack.pushPose();
        poseStack.scale(scalingFactor, scalingFactor, scalingFactor);

        this.drawString(poseStack, text, x / scalingFactor, y / scalingFactor, color);

        poseStack.popPose();
    }

    public void drawString(final PoseStack poseStack, final String text, final float x, final float y, final int color) {
        Wrapper.font().drawShadow(poseStack, text, x, y, color);
    }


    public boolean isToggled() {
        final Hack hack = HackManager.getHackE("Hud");
        if (hack != null) {
            return hack.isBooleanValue(name);
        }
        return false;
    }

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

    public void onRender(final PoseStack poseStack, final boolean debug) {
    }

    public void onDebug(final PoseStack poseStack, final int mouseX, final int mouseY) {
        if (this.dragging){
            this.x = mouseX;
            this.y = mouseY;
        }
        this.onRender(poseStack, true);
    }

}