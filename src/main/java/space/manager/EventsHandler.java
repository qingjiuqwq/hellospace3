/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.manager;

import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.lwjgl.glfw.GLFW;
import space.utils.Utils;
import space.utils.Wrapper;
import space.value.HaCd;

public class EventsHandler {

    // 注意!!!
    // MinecraftForge.EVENT_BUS.register(this);
    // 因为与 Forge 安全政策冲突，所有会出现报错，无法使用，等情况
    // 在未来更新可能会再次开放
    public EventsHandler() {
        // 事件优先级
        // 是否接收已取消的事件
        // 目标事件类型
        // 处理方法引用
        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                ViewportEvent.ComputeCameraAngles.class,
                this::onCameraSetup
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                InputEvent.Key.class,
                this::onKeyInput
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                TickEvent.ClientTickEvent.class,
                this::onClientTick
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                PlayerInteractEvent.LeftClickBlock.class,
                this::onLeftClickBlock
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                InputEvent.MouseButton.class,
                this::onMouseInputEvent
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                LivingAttackEvent.class,
                this::onLivingAttack
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                PlayerInteractEvent.RightClickItem.class,
                this::onRightClickItem
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                RenderGuiEvent.class,
                this::onRenderGameOverlay
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                AttackEntityEvent.class,
                this::onAttackEntity
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                MovementInputUpdateEvent.class,
                this::onInputUpdateEvent
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                LivingEvent.LivingTickEvent.class,
                this::onLivingUpdate
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                PlayerEvent.class,
                this::onPlayerUpdate
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                TickEvent.PlayerTickEvent.class,
                this::onPlayerTick
        );

        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                false,
                RenderLevelStageEvent.class,
                this::onRender3D
        );

    }

    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        try {
            if (Utils.nullCheck()) {
                HackManager.onCameraSetup(event);
            }
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onKeyInput(final InputEvent.Key event) {
        if (Utils.nullCheck()) {
            try {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    int key = event.getKey();
                    for (final HaCd haCd : HackManager.getAll()) {
                        if (haCd.getKey() == key) {
                            haCd.toggle();
                        }
                    }
                }
            } catch (RuntimeException ex) {
                ex.fillInStackTrace();
            }
        }
    }


    public void onClientTick(final TickEvent.ClientTickEvent event) {
        try {
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
        try {
            if (Utils.nullCheck()) {
                HackManager.onLeftClickBlock(event.getPos());
            }
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onMouseInputEvent(final InputEvent.MouseButton event) {
        try {
            if (Utils.nullCheck()) {
                HackManager.onMouseInputEvent(event);
            }
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onLivingAttack(final LivingAttackEvent event) {
        try {
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onRightClickItem(final PlayerInteractEvent.RightClickItem event) {
        try {
            if (Utils.nullCheck()) {
                HackManager.onRightClickItem(event);
            }
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onRenderGameOverlay(final RenderGuiEvent event) {
        try {
            if (Utils.nullCheck()) {
                HackManager.onRender(event.getGuiGraphics());
            }
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onAttackEntity(final AttackEntityEvent event) {
        try {
            if (Utils.nullCheck()) {
                HackManager.onAttack(event.getTarget());
            }
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onInputUpdateEvent(final MovementInputUpdateEvent event) {
        try {
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onLivingUpdate(final LivingEvent.LivingTickEvent event) {
        try {
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onPlayerUpdate(final PlayerEvent event) {
        try {
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        try {
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }


    public void onRender3D(final RenderLevelStageEvent event) {
        try {
            if (Utils.nullCheck() && !Wrapper.mc().options.hideGui) {
                HackManager.onRender(event.getPoseStack());
            }
            HackManager.onAllTick();
        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

}