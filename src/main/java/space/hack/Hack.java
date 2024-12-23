/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import space.utils.Connection;
import space.value.HaCd;

public class Hack extends HaCd
{
    public boolean Sprint;
    public boolean AimAssist;
    public boolean AutoTool;
    public boolean show;
    public boolean toggled;
    public final HackCategory category;

    public Hack(final String name, final HackCategory category) {
        super(name);
        this.category = category;
        this.toggled = false;
        this.show = true;
        this.Sprint = true;
        this.AimAssist = true;
        this.AutoTool = true;
    }

    public void toggle() {
        if (!this.isToggled()) {
            this.toggled = true;
            this.onEnable();
        } else {
            this.toggled = false;
            this.onDisable();
        }
    }


    public boolean isToggled() {
        return this.toggled;
    }

    public void isToggled(final boolean toggled) {
        if (this.toggled != toggled) {
            toggle();
        }
    }

    public boolean isShow() {
        return this.show;
    }

    public void setShow(final boolean show) {
        this.show = show;
    }

    public HackCategory isCategory(){
        return this.category;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onAllTick() {
    }

    public void onLeftClickBlock(final BlockPos event) {
    }

    public void onMouseInputEvent(final InputEvent.MouseInputEvent event) {
    }

    public void onRightClickItem(final Object event) {
    }

    public void onAttack(final Entity event) {
    }

    public void onCameraSetup(final EntityViewRenderEvent.CameraSetup event) {
    }

    public void onRender(final PoseStack poseStack) {
    }

    public boolean onPacket(final Object packet, final Connection.Side side) {
        return true;
    }

}
