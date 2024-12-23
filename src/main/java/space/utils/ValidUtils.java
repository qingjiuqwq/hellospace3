/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import space.hack.Hack;
import space.hack.another.AntiBot;
import space.manager.HackManager;
import space.value.Mode;
import space.value.ModeValue;

public class ValidUtils
{

    public static LivingEntity SimpleUpdate(final int Fov, final double Range, final String Priority, final int hurtTime) {
        double TargetRange = -1;
        LivingEntity LTarget = null;
        for (Object object : Utils.getEntityList()) {
            if (object instanceof LivingEntity target) {
                int check = check(target, Fov, Range, hurtTime);
                if (check == 0) {
                    double Distance = Priority.equals("Range") ? getDistanceToEntityBox(target) : target.getHealth();
                    if (TargetRange == -1 || Distance < TargetRange){
                        LTarget = target;
                        TargetRange = Distance;
                    }
                }else {
                    //System.out.print("[" + check + "]");
                }
            }
        }
        return LTarget;
    }

    public static ModeValue isPriority(final String name) {
        return new ModeValue(name, new Mode("Range", true), new Mode("Health"));
    }

    public static int check (final LivingEntity target, final int Fov, final double Range, final int hurtTim){
        if (!Wrapper.player().hasLineOfSight(target)){
            return 1;
        }else if(target instanceof ArmorStand || target.isSpectator()){
            return 2;
        }else if(!isValidEntity(target)) {
            return 3;
        }else if(!isInAttackRange(target, Range)){
            return 4;
        }else if(Fov != 360 && !ValidUtils.isInAttackFOV(target,  Fov / 2)){
            return 5;
        }else if(target.getY() - Wrapper.player().getY() > 2){
            return 6;
        }else if(!target.isAlive() || target.deathTime > 0){
            return 7;
        }else if (hurtTim != -1 && target.hurtTime > hurtTim){
            return 8;
        }

        if (target instanceof Player player){
            if(ValidUtils.isBot(player)){
                return 9;
            }else if(!ValidUtils.isTeam(player)){
                return 10;
            }
        }

        return 0;
    }

    public static boolean isTeam(final Player player) {
        final Hack hack = HackManager.getHackE("Teams");
        if (hack != null && hack.isToggled()) {
            String Mode = hack.isToggledMode("Mode");
            if (Mode.equals("Base")) {
                if (player.getTeam() != null && Wrapper.player().getTeam() != null && player.getTeam() == Wrapper.player().getTeam()){
                    return true;
                }
            }else return Utils.checkEnemyColor(player);
        }
        return true;
    }

    public static boolean isBot(final Player entity) {
        final Hack hack = HackManager.getHackE("AntiBot");
        if (hack != null && hack.isToggled()) {
            return AntiBot.isBot(entity,hack);
        }
        return false;
    }

    public static boolean isInAttackFOV(final Entity entity, final int maxAngle) {
        Vec3 playerPosition = Wrapper.player().position();
        Vec3 playerLookDirection = Wrapper.player().getViewVector(1.0F).normalize();
        Vec3 entityPosition = entity.position();
        Vec3 toEntity = entityPosition.subtract(playerPosition).normalize();

        double dotProduct = playerLookDirection.dot(toEntity);

        double angleRadians = Math.acos(dotProduct);

        double angleDegreesComputed = Math.toDegrees(angleRadians);

        return angleDegreesComputed <= maxAngle;
    }


    public static boolean isInAttackRange(final Entity entity, final double range) {
        double Distance = getDistanceToEntityBox(entity);
        return Distance <= range && Distance > 0.4;

    }

    public static boolean isValidEntity(final LivingEntity target) {
        final Hack hack = HackManager.getHackE("Targets");
        if (hack == null) {
            return false;
        }
        if (target instanceof Player){
            if (hack.isBooleanValue("Players")){
                return isValidEntity(target, hack);
            }
        }else if (hack.isBooleanValue("Mobs")){
            return isValidEntity(target, hack);
        }

        return false;
    }

    public static boolean isValidEntity(final LivingEntity target, final Hack hack) {
        if (target.isInvisible()) {
            return hack.isBooleanValue("Invisible");
        }else if (target.isSleeping()){
            return hack.isBooleanValue("Sleeping");
        }
        return true;
    }

    public static double getDistanceToEntityBox(final Entity target) {
        Vec3 eyes = Wrapper.player().getEyePosition();
        Vec3 pos = getNearestPointBB(eyes, target.getBoundingBox());
        double xDist = Math.abs(pos.x - eyes.x);
        double yDist = Math.abs(pos.y - eyes.y);
        double zDist = Math.abs(pos.z - eyes.z);
        return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    private static Vec3 getNearestPointBB(final Vec3 point, final AABB box) {
        double x = Math.max(box.minX, Math.min(point.x, box.maxX));
        double y = Math.max(box.minY, Math.min(point.y, box.maxY));
        double z = Math.max(box.minZ, Math.min(point.z, box.maxZ));
        return new Vec3(x, y, z);
    }

}