/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import space.utils.Connection;
import space.value.HaCd;

public class Hack extends HaCd {
    private final HackCategory category;
    public boolean warn;
    public boolean sprint = true;
    public boolean aimAssist = true;
    public boolean autoTool = true;
    private boolean show = true;
    private boolean toggled = false;

    public Hack(final String name, final HackCategory category) {
        this(name, category, false);
    }

    public Hack(final String name, final HackCategory category, final boolean warn) {
        super(name);
        this.category = category;
        this.warn = warn;
    }

    @Override
    public void toggle() {
        if (!this.isToggled()) {
            this.onEnable();
            this.toggled = true;
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

    @Override
    public String isCategory() {
        return this.category.toString();
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onAllTick() {
    }

    public void onLeftClickBlock(final BlockPos event) {
    }

    public void onMouseInputEvent(final InputEvent event) {
    }

    public void onRightClickItem(final Object event) {
    }

    public void onAttack(final Entity event) {
    }

    public void onCameraSetup(final ViewportEvent.ComputeCameraAngles event) {
    }

    public void onRender(final GuiGraphics graphics) {
    }

    public void onRender(final PoseStack poseStack) {
    }

    /**
     * @param packet 发包
     * @param side   客户端与服务端
     * @return 0 取消发送 1 发送 2 发送并修复包 3 取消并修复
     */
    public int onPacket(final Object packet, final Connection.Side side) {
        return 1;
    }

    /**
     * @param packet 发包
     * @param side   客户端与服务端
     * @param send   发送修复 还是 取消修复
     *               客户端与服务端同步时，修复数据包
     *               全称 onPacketFixes
     */
    public void onPFixes(final Object packet, final Connection.Side side, final boolean send) {
    }

}
