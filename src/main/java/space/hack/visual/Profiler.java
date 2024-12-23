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
import com.mojang.math.Quaternion;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.RenderUtils;
import space.utils.Utils;
import space.utils.Wrapper;
import space.value.IntValue;
import space.value.Mode;
import space.value.ModeValue;
import space.value.NumberValue;

import java.awt.*;

public class Profiler extends Hack {

    public final ModeValue mode;
    public final NumberValue scaleText;
    public final NumberValue scaleBox;
    public final IntValue heightBox;

    public Profiler() {
        super("Profiler", HackCategory.Visual);
        this.mode = new ModeValue("Mode", new Mode("Simple", true), new Mode("Flipped"));
        this.scaleText = new NumberValue("Scale-Text", 0.20, 0.0, 0.3);
        this.scaleBox = new NumberValue("Scale-Box", 0.27, 0.0, 0.3);
        this.heightBox = new IntValue("Height-Box", 40, 0, 50);
        this.addValue(this.mode, this.scaleText, this.scaleBox, this.heightBox);
    }

    @Override
    public void onRender(final PoseStack poseStack) {
        for (final Object object : Utils.getEntityList()) {
            if (object instanceof LivingEntity entity) {
                Vec3 Pos = Utils.getDoubles(entity);
                Double scale = this.scaleText.getValue();
                if (scale > 0){
                    drawEntityText(poseStack, entity, Pos, scale.floatValue() / 10F, Utils.rainbow(10, 2));
                }
                scale = this.scaleBox.getValue();
                if (scale > 0 && !entity.isDeadOrDying()){
                    renderEntityBoundingBox(poseStack, entity, Pos, scale.floatValue() / 10F, Utils.rainbow(9, 0));
                }
            }
        }
    }

    private void drawEntityText(final PoseStack poseStack, final LivingEntity entity, final Vec3 Pos, final float scale, final Color color) {
        String health = Utils.health(entity.getHealth());
        String maxhealth = Utils.health(entity.getMaxHealth());

        String text = entity.getName().getString() +
                ": " + Utils.getDistance(entity) +
                " Health: " + health + "/" + maxhealth;

        poseStack.pushPose();
        Font fontRenderer = Wrapper.font();

        poseStack.translate(Pos.x, Pos.y + entity.getBbHeight() + 0.5, Pos.z);
        poseStack.mulPose(new Quaternion(0.0F, -Wrapper.getRenderManager().camera.getYRot() + 180, 0.0F, true));
        poseStack.scale(scale, -scale, scale);

        fontRenderer.drawShadow(poseStack, text, -60, -3, color.getRGB());

        poseStack.popPose();
    }

    public void renderEntityBoundingBox(final PoseStack poseStack, final LivingEntity entity, final Vec3 Pos, final float scale, final Color color) {

        RenderSystem.disableDepthTest();
        poseStack.pushPose();
        poseStack.translate(Pos.x, Pos.y, Pos.z);
        poseStack.mulPose(new Quaternion(0.0F, -Wrapper.getRenderManager().camera.getYRot(), 0.0F, true));
        poseStack.scale(scale, scale, scale);

        double health = entity.getHealth();
        double maxhealth = entity.getMaxHealth();

        int height = this.heightBox.getValue() + 30;
        int newValue = calculateHealthBarValue(health, maxhealth, height);

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