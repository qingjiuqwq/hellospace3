/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.visual;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.RenderUtils;
import space.utils.Utils;
import space.utils.ValidUtils;
import space.utils.Wrapper;
import space.value.IntValue;
import space.value.Mode;
import space.value.ModeValue;

import java.awt.*;

public class Profiler extends Hack {

    private final ModeValue mode;
    private final IntValue range;

    public Profiler() {
        super("Profiler", HackCategory.Visual);
        this.mode = new ModeValue("Mode", new Mode("Simple", true));
        this.range = new IntValue("Range", 30, 10, 100);
        this.addValue(this.mode, this.range);
    }

    @Override
    public void onRender(final GuiGraphics graphics) {
        PoseStack poseStack = graphics.pose();
        for (final Object object : Utils.getEntityList()) {
            if (object instanceof LivingEntity entity) {
                if (ValidUtils.getDistanceToEntityBox(entity) <= this.range.getValue() && !ValidUtils.isValidEntity(entity)) {
                    Vec3 pos = Utils.getDoubles(entity);
                    drawEntityText(graphics, poseStack, entity, pos, Utils.rainbow(10, 4));
                    if (!entity.isDeadOrDying()) {
                        renderEntityBoundingBox(poseStack, entity, pos, Utils.rainbow(9, 5));
                    }
                }
            }
        }
    }

    private void drawEntityText(GuiGraphics graphics, PoseStack poseStack, final LivingEntity entity, final Vec3 pos, final Color color) {
        String health = Utils.health(entity.getHealth());
        String maxHealth = Utils.health(entity.getMaxHealth());

        String text = entity.getName().getString() +
                ": " + Utils.getDistance(entity) +
                " Health: " + health + "/" + maxHealth;

        poseStack.pushPose();

        poseStack.translate(pos.x, pos.y + entity.getBbHeight() + 0.5, pos.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-Wrapper.getRenderManager().camera.getYRot() + 180));
        poseStack.scale(0.02f, -0.02f, 0.02f);

        Wrapper.drawString(graphics, text, -60, -3, color.getRGB());

        poseStack.popPose();
    }

    private void renderEntityBoundingBox(final PoseStack poseStack, final LivingEntity entity, final Vec3 pos, final Color color) {
        RenderSystem.disableDepthTest();
        poseStack.pushPose();
        poseStack.translate(pos.x, pos.y, pos.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-Wrapper.getRenderManager().camera.getYRot()));
        poseStack.scale(0.027f, 0.027f, 0.027f);

        double health = entity.getHealth();
        double maxHealth = entity.getMaxHealth();

        int height = 70;
        int newValue = calculateHealthBarValue(health, maxHealth, height);

        RenderUtils.drawRect(poseStack, 17, -1, 23, height + 1, Color.BLACK.getRGB());
        RenderUtils.drawRect(poseStack, 18, 0, 22, newValue, color.getRGB());

        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }

    private int calculateHealthBarValue(final double health, final double maxHealth, final int height) {
        return (int) (this.mode.getValue("Simple").isToggled()
                ? (health / maxHealth) * height
                : (1.0 - (health / maxHealth)) * height);
    }
}
