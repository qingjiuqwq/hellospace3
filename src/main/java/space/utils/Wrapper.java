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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class Wrapper {

    public static Minecraft mc() {
        return Minecraft.getInstance();
    }

    public static ClientLevel level() {
        return mc().level;
    }

    public static MultiPlayerGameMode controller() {
        return mc().gameMode;
    }

    public static LocalPlayer player() {
        return mc().player;
    }

    public static boolean isKeyMove() {
        return !Wrapper.player().input.down && !Wrapper.player().input.up && !Wrapper.player().input.left && !Wrapper.player().input.right;
    }

    public static Font font() {
        return mc().font;
    }

    public static void drawString(final GuiGraphics graphics, final String text, final float x, final float y, final int color) {
        graphics.drawString(
                Wrapper.font(),
                text,
                x,
                y,
                color,
                true
        );
    }

    public static void drawString(final GuiGraphics graphics, final String text, final float x, final float y, final Color color) {
        drawString(
                graphics,
                text,
                x,
                y,
                color.getRGB()
        );
    }

    public static void swing(InteractionHand interaction) {
        player().swing(interaction);
    }

    public static void swing(InteractionHand interaction, boolean server) {
        if (server) {
            swing(interaction);
        } else {
            sendPacket(new ServerboundSwingPacket(interaction));
        }
    }

    public static int getFps() {
        return mc().getFps();
    }

    public static EntityRenderDispatcher getRenderManager() {
        return mc().getEntityRenderDispatcher();
    }

    public static void SetMotionY(final double y) {
        Vec3 vec32 = player().getDeltaMovement();
        double x = vec32.x;
        double z = vec32.z;
        player().setDeltaMovement(x, y, z);
    }

    public static void sendPacket(final Packet<?> packet) {
        player().connection.send(packet);
    }

    public static void attack(Entity target) {
        mc().gameMode.attack(player(), target);
    }

    public static boolean isItemStack(final Item item) {
        for (int i = 0; i < Wrapper.player().getInventory().items.size(); ++i) {
            if (Wrapper.player().getInventory().getItem(i).getItem().equals(item)) {
                return false;
            }
        }
        for (int i = 0; i < Wrapper.player().getInventory().armor.size(); ++i) {
            if (Wrapper.player().getInventory().getItem(i).getItem().equals(item)) {
                return false;
            }
        }
        return true;
    }

}
