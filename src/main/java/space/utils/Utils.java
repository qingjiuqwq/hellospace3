/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
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
import java.awt.*;
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

    public static void upRotations(final Entity target, final float speed) {
        float[] rotations = LowRotations(target, speed);
        if (rotations != null) {
            Wrapper.player().setYRot(rotations[0]);
        }
    }

    public static float[] LowRotations(final Entity target, final float speed) {
        float[] rotations = getSimpleRotations(target);
        return speed == 11 ?  rotations : updateRotation(rotations[1], rotations[0], speed);
    }

    public static float[] getSimpleRotations(final Entity targetEntity) {

        double playerX = Wrapper.player().getX();
        double playerZ = Wrapper.player().getZ();
        double playerY;

        boolean random = random(1, 2) == 1;

        if (random) {
            playerY = Wrapper.player().getY() + Wrapper.player().getEyeHeight();
        } else {
            playerY = Wrapper.player().getBoundingBox().minY + Wrapper.player().getEyeHeight();
        }

        double deltaX = targetEntity.getX() - playerX;
        double deltaZ = targetEntity.getZ() - playerZ;
        double deltaY;

        if (random) {
            deltaY = targetEntity.getY() + targetEntity.getBbHeight() / 2.0 - playerY;
        } else {
            deltaY = targetEntity.getY() + targetEntity.getEyeHeight() - playerY;
        }

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double yRot = Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        double xRot = -Math.toDegrees(Math.atan2(deltaY, distance));

        return new float[]{(float) yRot, clampXRot(xRot)};
    }

    public static float[] updateRotation(final float myYRot, final float myXRot, final float yRot, final float xRot, final float speed) {

        float currentXRot = myXRot;
        float currentYRot = myYRot;

        if (Math.abs(currentXRot - xRot) < 0.01f && Math.abs(currentYRot - yRot) < 0.01f) {
            return null;
        }

        if (Math.abs(currentXRot - xRot) > 0.01f) {
            currentXRot += Math.signum(xRot - currentXRot) * Math.min(speed, Math.abs(xRot - currentXRot));
        }
        if (Math.abs(currentYRot - yRot) > 0.01f) {
            currentYRot += Math.signum(yRot - currentYRot) * Math.min(speed, Math.abs(yRot - currentYRot));
        }

        return new float[]{currentYRot, currentXRot};
    }

    public static float[] updateRotation(final float xRot, final float yRot, final float speed) {
        return updateRotation(Wrapper.player().getYRot() , Wrapper.player().getXRot(), yRot, xRot, speed);
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

    public static void processAttack(final Entity target, final String mode, final boolean swing) {
        ItemStack offHandItem = Wrapper.player().getOffhandItem();
        if(!offHandItem.isEmpty() && offHandItem.isEdible()) {
            switch (mode) {
                case "Packet":
                    Wrapper.sendPacket(new ServerboundUseItemPacket(InteractionHand.OFF_HAND));
                    Wrapper.swing(InteractionHand.OFF_HAND, target);
                    break;
                case "Mouse":
                    KeyMapping.click(Wrapper.mc().options.keyUse.getKey());
                    break;
                case "UseItem":
                    Wrapper.controller().useItem(Wrapper.player(), Wrapper.level(), InteractionHand.OFF_HAND);
                    break;
            }
        }
        if (swing) {
            Wrapper.swing(InteractionHand.MAIN_HAND, target);
        }
        Wrapper.sendPacket(ServerboundInteractPacket.createAttackPacket(target, Wrapper.player().isShiftKeyDown()));
    }

    public static LivingEntity getTarget(final Hack hack) {
        return getTarget(hack, "MouseClick", "Fov", "Range", "HurtTime");
    }

    public static LivingEntity getTarget(final Hack hack, final String mouseClick, final String fov, final String range, final String hurtTime) {
        if (hack.isBooleanValue(mouseClick) && !(Wrapper.mc().options.keyAttack.isDown() || Wrapper.mc().options.keyUse.isDown())) {
            hack.Sprint = true;
            hack.AimAssist = true;
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
}