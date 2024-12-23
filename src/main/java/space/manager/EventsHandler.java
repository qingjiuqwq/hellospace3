/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.manager;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import space.hack.Hack;
import space.hack.hud.Hud;
import space.utils.Utils;
import space.utils.Wrapper;

public class EventsHandler {

    public static String gameIp = "None";

    @SubscribeEvent
    public void onCameraSetup(final EntityViewRenderEvent.CameraSetup event) {
        try {
            if (Utils.nullCheck()) {
                HackManager.onCameraSetup(event);
            }
            HackManager.onSever();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Utils.nullCheck()) {
            try {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    int key = event.getKey();
                    for (final Hack hack : HackManager.getHack()) {
                        if (hack.getKey() == key) {
                            hack.toggle();
                        }
                    }

                    for (final Hud hud : HackManager.getHud()) {
                        if (hud.getKey() == key) {
                            hud.toggle();
                        }
                    }
                    MinecraftServer server = Wrapper.player().getServer();
                    gameIp = server == null ? "None" : server.getLocalIp();
                }
            } catch (RuntimeException ex) {
                ex.fillInStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        try {
            HackManager.onAllTick();
            HackManager.onSever();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
        if (Utils.nullCheck()) {
            try {
                HackManager.onLeftClickBlock(event.getPos());
            } catch(RuntimeException ex){
                ex.fillInStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onMouseInputEvent(final InputEvent.MouseInputEvent event) {
        if (Utils.nullCheck()) {
            try {
                HackManager.onMouseInputEvent(event);
            } catch(RuntimeException ex){
                ex.fillInStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(final LivingAttackEvent event) {
        try {
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem event) {
        if (Utils.nullCheck()) {
            try {
                HackManager.onRightClickItem(event);
            } catch (RuntimeException ex) {
                ex.fillInStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Text event) {
        try {
            if (Utils.nullCheck()) {
                HackManager.onRender(event.getMatrixStack());
            }
            HackManager.onAllTick();
            HackManager.onSever();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent event) {
        if (Utils.nullCheck()) {
            try {
                HackManager.onAttack(event.getTarget());
            } catch (RuntimeException ex) {
                ex.fillInStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onInputUpdateEvent(final MovementInputUpdateEvent event) {
        try {
            HackManager.onAllTick();
            HackManager.onSever();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
        try {
            HackManager.onAllTick();
            HackManager.onSever();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(final PlayerEvent.LivingUpdateEvent event) {
        try {
            HackManager.onAllTick();
            HackManager.onSever();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        try {
            HackManager.onAllTick();
            HackManager.onSever();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @SubscribeEvent
    public void onRender3D(final RenderLevelLastEvent event) {
        try {
            if (Utils.nullCheck() && !Wrapper.mc().options.hideGui) {
                HackManager.onRender(event.getPoseStack());
            }
            HackManager.onSever();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

}