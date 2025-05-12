/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import space.hack.Hack;
import space.utils.vec.VecPos;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

public class Utils {
    public static boolean nullCheck() {
        return Wrapper.player() != null && Wrapper.level() != null;
    }

    public static String health(double health) {
        return health(health, true);
    }

    public static String health(final double health, final boolean Point) {
        String num = String.format("%.2f", health);
        if (Point && num.contains(".00")) {
            return num.substring(0, num.length() - 3);
        }
        return num;
    }

    public static String getDistance(final Entity target) {
        return getDistance(target, false);
    }

    public static String getDistance(final Entity target, final boolean Point) {
        return health(ValidUtils.getDistanceToEntityBox(target), Point);
    }

    public static Vec3 getDoubles(final LivingEntity entity) {
        final double renderPosX = Wrapper.getRenderManager().camera.getPosition().x();
        final double renderPosY = Wrapper.getRenderManager().camera.getPosition().y();
        final double renderPosZ = Wrapper.getRenderManager().camera.getPosition().z();
        final double xPos = entity.xOld + (entity.getX() - entity.xOld) * Wrapper.mc().getFrameTime() - renderPosX;
        final double yPos = entity.yOld + (entity.getY() - entity.yOld) * Wrapper.mc().getFrameTime() - renderPosY;
        final double zPos = entity.zOld + (entity.getZ() - entity.zOld) * Wrapper.mc().getFrameTime() - renderPosZ;
        return new Vec3(xPos, yPos, zPos);
    }

    public static int random(final int min, final int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static Color rainbow(final int speed, final int index) {
        long currentTime = System.currentTimeMillis() / speed + index;
        int angle = (int) (currentTime % 360);
        float hue = angle / 360.0f;
        return Color.getHSBColor(hue, 0.7f, 1.0f);
    }

    public static void addEffect(final int id, final int duration, final int amplifier) {
        Wrapper.player().addEffect(new MobEffectInstance(Objects.requireNonNull(MobEffect.byId(id)), duration, amplifier));
    }

    public static boolean checkEnemyColor(final Player enemy) {
        final int colorEnemy0 = getPlayerArmorColor(enemy, enemy.getInventory().armor.get(0));
        final int colorEnemy2 = getPlayerArmorColor(enemy, enemy.getInventory().armor.get(1));
        final int colorEnemy3 = getPlayerArmorColor(enemy, enemy.getInventory().armor.get(2));
        final int colorEnemy4 = getPlayerArmorColor(enemy, enemy.getInventory().armor.get(3));
        final int colorPlayer0 = getPlayerArmorColor(Wrapper.player(), Wrapper.player().getInventory().armor.get(0));
        final int colorPlayer2 = getPlayerArmorColor(Wrapper.player(), Wrapper.player().getInventory().armor.get(1));
        final int colorPlayer3 = getPlayerArmorColor(Wrapper.player(), Wrapper.player().getInventory().armor.get(2));
        final int colorPlayer4 = getPlayerArmorColor(Wrapper.player(), Wrapper.player().getInventory().armor.get(3));
        return (colorEnemy0 != colorPlayer0 || colorPlayer0 == -1 || colorEnemy0 == 1) && (colorEnemy2 != colorPlayer2 || colorPlayer2 == -1 || colorEnemy2 == 1) && (colorEnemy3 != colorPlayer3 || colorPlayer3 == -1 || colorEnemy3 == 1) && (colorEnemy4 != colorPlayer4 || colorPlayer4 == -1 || colorEnemy4 == 1);
    }

    public static int getPlayerArmorColor(final Entity player, final ItemStack stack) {
        if (player != null && stack != null) {
            if (stack.getItem() instanceof ArmorItem itemArmor) {
                if (itemArmor != Items.LEATHER) {
                    return itemArmor.getBarColor(stack);
                }
            }
        }
        return -1;
    }

    public static VecPos upRotations(final Entity target, final int speed) {
        VecPos vecPos = getSimpleRotations(target);

        float currentY = Wrapper.player().getYRot();
        float currentX = Wrapper.player().getXRot();

        float angleDiffY = (vecPos.yRot - currentY + 360) % 360;
        if (angleDiffY > 180) angleDiffY -= 360;

        float angleDiffX = vecPos.xRot - currentX;

        if (Math.abs(angleDiffY) <= 0.01f && Math.abs(angleDiffX) <= 0.01f) {
            return null;
        }

        float stepY = Math.signum(angleDiffY) * Math.min(speed, Math.abs(angleDiffY));
        float newY = currentY + stepY;
        newY = (newY % 360 + 360) % 360;
        Wrapper.player().setYRot(newY);

        float stepX = Math.signum(angleDiffX) * Math.min(speed, Math.abs(angleDiffX));
        float newX = currentX + stepX;
        newX = Math.max(-90, Math.min(90, newX));

        return new VecPos(newY, newX);
    }

    public static VecPos getSimpleRotations(final Entity targetEntity) {
        return getSimpleRotations(targetEntity, random(1, 2));
    }

    public static VecPos getSimpleRotations(final Entity targetEntity, final int deltaYMode) {
        return getSimpleRotations(targetEntity, Wrapper.player().position(), deltaYMode);
    }

    public static VecPos getSimpleRotations(final Entity targetEntity, final double playerX, final double playerY, final double playerZ, final int deltaYMode) {

        double deltaX = targetEntity.getX() - playerX;
        double deltaZ = targetEntity.getZ() - playerZ;
        double deltaY = getDeltaY(targetEntity, deltaYMode, playerY + Wrapper.player().getEyeHeight());

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double yRot = Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        double xRot = -Math.toDegrees(Math.atan2(deltaY, distance));

        return new VecPos((float) yRot, clampXRot(xRot));
    }

    public static double getDeltaY(Entity targetEntity, int deltaYMode, double playerY) {
        if (deltaYMode == 1) {
            return targetEntity.getY() + targetEntity.getEyeHeight() - playerY;
        } else {
            return targetEntity.getY() + targetEntity.getBbHeight() / 2.0 - playerY;
        }
    }

    public static VecPos getSimpleRotations(final Entity targetEntity, final Vec3 player, final int deltaYMode) {
        return getSimpleRotations(targetEntity, player.x, player.y, player.z, deltaYMode);
    }

    private static float clampXRot(final double xRot) {
        return (float) Math.min(Math.max(xRot, -90), 90);
    }

    public static void removeEffect(final int id) {
        Wrapper.player().removeEffect(Objects.requireNonNull(MobEffect.byId(id)));
    }

    public static Iterable<Entity> getEntityList() {
        return Wrapper.level().entitiesForRendering();
    }

    public static Packet<?> processAttack(final Entity target, final String mode, final boolean swing) {
        ItemStack offHandItem = Wrapper.player().getOffhandItem();
        if (!offHandItem.isEmpty() && offHandItem.getItem() == Items.SHIELD) {
            switch (mode) {
                case "Packet":
                    // Wrapper.sendPacket(new ServerboundUseItemPacket(InteractionHand.OFF_HAND, Wrapper.player()));
                    Wrapper.swing(InteractionHand.OFF_HAND, swing);
                    break;
                case "Mouse":
                    KeyMapping.click(Wrapper.mc().options.keyUse.getKey());
                    break;
                case "UseItem":
                    Wrapper.controller().useItem(Wrapper.player(), InteractionHand.OFF_HAND);
                    break;
            }
        }
        // Wrapper.attack(target);
        return ServerboundInteractPacket.createAttackPacket(target, Wrapper.player().isShiftKeyDown());
    }

    public static LivingEntity getTarget(final Hack hack) {
        return getTarget(hack, "MouseClick", "Fov", "Range", "HurtTime");
    }

    public static LivingEntity getTarget(final Hack hack, final String mouseClick, final String fov, final String range, final String hurtTime) {
        if (hack.isBooleanValue(mouseClick) && !(Wrapper.mc().options.keyAttack.isDown() || Wrapper.mc().options.keyUse.isDown())) {
            hack.sprint = true;
            hack.aimAssist = true;
            return null;
        }

        int fovX = hack.isIntValue(fov);
        double rangeX = hack.isNumberValue(range);
        int hurtTimeX = hack.isIntValue(hurtTime);

        if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult &&
                hitResult.getEntity() instanceof LivingEntity livingEntity &&
                ValidUtils.check(livingEntity, fovX, rangeX, hurtTimeX) == 0) {
            return livingEntity;
        }

        return null;
    }

    public static String base64(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }
}