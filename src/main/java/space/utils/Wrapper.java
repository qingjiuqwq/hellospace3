/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class Wrapper
{

    public static Minecraft mc() {
        return Minecraft.getInstance();
    }

    public static ClientLevel level(){
        return mc().level;
    }

    public static MultiPlayerGameMode controller() {
        return mc().gameMode;
    }

    public static LocalPlayer player() {
        return mc().player;
    }

    public static Font font(){
        return mc().font;
    }

    public static void swing(InteractionHand interaction){
        Wrapper.player().swing(interaction);
    }

    public static void swing(InteractionHand interaction, Entity target){
        if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult) {
            if (hitResult.getEntity() == target) {
                Wrapper.player().swing(interaction);
                return;
            }
        }
        Wrapper.player().swing(interaction, true);
    }

    public static EntityRenderDispatcher getRenderManager() {
        return Wrapper.mc().getEntityRenderDispatcher();
    }

    public static void SetMotionY(final double y){
        Vec3 vec32 = Wrapper.player().getDeltaMovement();
        double x = vec32.x;
        double z = vec32.z;
        Wrapper.player().setDeltaMovement(x, y, z);
    }

    public static void sendPacket(final Packet<?> packet) {
        player().connection.send(packet);
    }
}
