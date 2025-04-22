/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
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

    public static ResourceLocation drawHead(LivingEntity target) {
        if (Wrapper.mc().getConnection() == null) {
            return null;
        }

        if (target instanceof Animal an) {
            return an.getLootTable();
        }

        PlayerInfo playerInfo = Wrapper.mc().getConnection().getPlayerInfo(target.getUUID());
        if (playerInfo == null) {
            return null;
        }
        return playerInfo.getSkinLocation();
    }

    public static ResourceLocation drawHead2(LivingEntity target) {
        ResourceLocation r = drawHead(target);
        if (r == null) {
            return drawHead(Wrapper.player());
        }
        return r;
    }

    @Override
    public void onRender(final GuiGraphics graphics, final boolean debug) {
        Player target = null;

        if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult) {
            if (hitResult.getEntity() instanceof Player player) {
                if (ValidUtils.isInAttackRange(player, 10)) {
                    target = player;
                }
            }
        }

        if (target == null) {
            LivingEntity livingEntity = ValidUtils.SimpleUpdate(80, 4.4, "Range", 10);
            if (livingEntity instanceof Player player) {
                target = player;
            }
        }

        if (target == null) {
            if (debug) {
                target = Wrapper.mc().player;
            } else {
                return;
            }
        }

        if (target == null) {
            return;
        }

        float health = target.getHealth() < 0 ? 0 : target.getHealth();
        float healthPercentage = target.getHealth() / target.getMaxHealth();
        healthPercentage = Math.max(0, Math.min(1, healthPercentage));
        Color backgroundColor;
        if (healthPercentage < 0.5) {
            int redValue = (int) (255 * (1 - healthPercentage * 2));
            backgroundColor = new Color(255, redValue, redValue);
        } else {
            backgroundColor = Utils.rainbow(9, 1);
        }

        this.drawString(graphics, target.getName().getString(), this.x - 1, this.y - 10, Color.WHITE);

        this.drawString(graphics, "Range: " + Utils.getDistance(target), 9, this.x, this.y, Color.WHITE);
        this.drawString(graphics, "Health: " + Utils.health(target.getHealth(), false), 9, this.x, this.y + 10, Color.WHITE);

        drawHead(graphics, drawHead2(target), this.x - 31, this.y - 9);

        Wrapper.drawString(graphics, "\u2764", this.x + 67, this.y + 10, Utils.rainbow(9, 2));

        RenderUtils.drawRect(graphics, (int) (this.x - 45 + ((health > 20 ? 20 : health) < 4 ? 15 : (health > 20 ? 20 : health) * 6)), this.y + 21, this.x - 31, this.y + 19, backgroundColor);

    }

    public void drawHead(final GuiGraphics graphics, final ResourceLocation skin, final int x, final int y) {

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, skin);

        graphics.blit(
                skin,
                x, y,
                28, 28,
                30, 33,
                36, 30,
                256, 256
        );

    }
}
