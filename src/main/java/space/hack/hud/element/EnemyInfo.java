/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import space.hack.hud.Hud;
import space.utils.RenderUtils;
import space.utils.Utils;
import space.utils.ValidUtils;
import space.utils.Wrapper;

import java.awt.*;

public class EnemyInfo extends Hud {

    public EnemyInfo() {
        super("EnemyInfo");
    }

    @Override
    public void onRender(final PoseStack poseStack, final boolean debug) {
        Player target = null;

        if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult) {
            if (hitResult.getEntity() instanceof Player player) {
                if(ValidUtils.isInAttackRange(player, 10)){
                    target = player;
                }
            }
        }

        if (target == null) {
            LivingEntity livingEntity = ValidUtils.SimpleUpdate(80, 4.4, "Range", 10);
            if (livingEntity == null) {
                if (debug){
                    target = Wrapper.mc().player;
                }else {
                    return;
                }
            }else if (livingEntity instanceof Player player) {
                target = player;
            }else {
                if (debug){
                    target = Wrapper.mc().player;
                }else {
                    return;
                }
            }
        }

        if (target == null) {
            return;
        }

        float health = target.getHealth() < 0 ? 0 : target.getHealth();
        float healthPercentage = target.getHealth() / target.getMaxHealth();
        healthPercentage = Math.max(0, Math.min(1, healthPercentage));
        int backgroundColor;
        if (healthPercentage < 0.5) {
            int redValue = (int)(255 * (1 - healthPercentage * 2));
            backgroundColor = new Color(255, redValue, redValue).getRGB();
        } else {
            backgroundColor = Utils.rainbow(9, 0).getRGB();
        }

        Wrapper.font().drawShadow(poseStack, target.getName(), this.x, this.y - 10, new Color(255, 255, 255).getRGB());

        Wrapper.font().draw(poseStack, "Distance: " + Utils.getDistance(target),this.x + 1, this.y, new Color(255,255,255).getRGB());
        Wrapper.font().draw(poseStack, "HP: " + Utils.health(target.getHealth()), this.x + 1, this.y + 10, new Color(255,255,255).getRGB());

        drawHead(poseStack, drawHead(target), this.x - 31, this.y - 9);

        Wrapper.font().draw(poseStack, Math.round(health) + "\u2764", this.x + 57, this.y + 10, backgroundColor);

        RenderUtils.drawRect(poseStack, (int) (this.x - 45 + ((health > 20 ? 20 : health) < 4 ? 15 : (health > 20 ? 20 : health) * 6)), this.y + 21, this.x - 31, this.y + 19, backgroundColor);

    }

    public ResourceLocation drawHead(LivingEntity target){
        if (Wrapper.mc().getConnection() == null){
            return null;
        }

        if (target instanceof Animal an) {
            return an.getLootTable();
        }

        PlayerInfo playerInfo = Wrapper.mc().getConnection().getPlayerInfo(target.getUUID());
        if (playerInfo == null){
            return null;
        }
        return playerInfo.getSkinLocation();
    }

    public void drawHead(final PoseStack poseStack, ResourceLocation skin, int x, int y) {
        Wrapper.mc().getTextureManager().bindForSetup(skin);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, skin);

        GuiComponent.blit(poseStack, x, y, 253, 248, 28, 28, 225, 221);
    }
}
