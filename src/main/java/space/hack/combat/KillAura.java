/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.combat;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import space.hack.Hack;
import space.hack.HackCategory;
import space.mixin.invoked.MotionEvent;
import space.utils.TimerUtils;
import space.utils.Utils;
import space.utils.ValidUtils;
import space.utils.Wrapper;
import space.utils.vec.VecPos;
import space.value.*;

public class KillAura extends Hack {

    private final ModeValue priority;
    private final IntValue maxCPS;
    private final IntValue minCPS;
    private final IntValue hurtTime;
    private final IntValue fov;
    private final TimerUtils timer;
    private final NumberValue range;
    private final ModeValue mode;
    private final ModeValue autoBlock;
    private final BooleanValue aac;
    private final BooleanValue swing;
    private final BooleanValue mouseClick;
    private final BooleanValue sprintMode;
    private final ModeValue rotation;
    private final IntValue rotationSpeed;
    private VecPos speedRot;
    private int modeRot = -1;
    private LivingEntity target;
    private boolean silentRotation;
    private boolean backRotation;
    private int countRot;
    private VecPos legalRot;
    private VecPos endRot;

    public KillAura() {
        super("KillAura", HackCategory.Combat, true, true);
        this.mode = new ModeValue("Mode", "Simple", "Legal", "Strict");
        this.priority = ValidUtils.isPriority("Priority");
        this.maxCPS = new IntValue("MaxCPS", 11, 1, 20);
        this.minCPS = new IntValue("MinCPS", 5, 1, 19);
        this.hurtTime = new IntValue("HurtTime", 10, 0, 10);
        this.range = new NumberValue("Range", 3.8, 1.0, 6.0);
        this.aac = new BooleanValue("AAC", false);
        this.swing = new BooleanValue("Swing", true);
        this.sprintMode = new BooleanValue("Sprint", true);
        this.rotation = new ModeValue("Rotation", "Silent", "Legal", "None");
        this.autoBlock = new ModeValue("AutoBlock", "Packet", "UseItem", "Mouse", "None");
        this.mouseClick = new BooleanValue("MouseClick", false);
        this.fov = new IntValue("Fov", 120, 1, 360);
        this.rotationSpeed = new IntValue("RSilentSpeed", 10, 1, 10);
        this.addValue(this.mode,
                new TextValue(28),
                this.maxCPS, this.minCPS,
                new TextValue(28),
                this.hurtTime, this.range,
                new TextValue(28),
                this.priority, this.aac, this.swing, this.sprintMode, this.mouseClick,
                new TextValue(28),
                this.rotation, this.autoBlock, this.fov, this.rotationSpeed,
                new TextValue(28)
        );
        this.silentRotation = false;
        this.timer = new TimerUtils();
    }

    @Override
    public void onEnable() {
        this.target = null;
        this.legalRot = null;
        this.backRotation = false;
    }

    @Override
    public void onDisable() {
        this.backRotation = true;
    }

    @Override
    public void onAllTick() {
        if ((this.aac.getValue() && Wrapper.mc().screen != null) ||
                (this.mouseClick.getValue() && !(Wrapper.mc().options.keyAttack.isDown() || Wrapper.mc().options.keyUse.isDown()))) {
            this.onEnable();
            this.onDisable();
            return;
        }

        LivingEntity target = Utils.getTarget(this);
        if (target == null) {
            target = ValidUtils.SimpleUpdate(this.fov.getValue(), this.range.getValue(), this.priority.getMode(), this.hurtTime.getValue());
        }

        if (target == null || !target.equals(this.target)) {
            this.modeRot = -1;
            this.target = target;
            this.silentRotation = false;
        }

        if (target == null) {
            this.backRotation = true;
            return;
        }

        boolean flag = false;
        if (this.rotation.equals("None") || this.mode.equals("Simple")) {
            flag = true;
        } else if (this.rotation.equals("Legal") || this.mode.equals("Legal")) {
            flag = Wrapper.mc().hitResult instanceof EntityHitResult hitResult && hitResult.getEntity().equals(target);
            this.legalRot = Utils.upRotations(target, this.rotationSpeed.getValue());
        } else if (this.mode.equals("Strict")) {
            flag = this.silentRotation;
        }

        if (flag) {
            this.aimAssist = false;

            if (!this.sprintMode.getValue()) {
                this.sprint = false;
            }

            if (this.timer.isDelay(this.minCPS.getValue(), this.maxCPS.getValue())) {
                Wrapper.sendPacket(Utils.processAttack(target, this.autoBlock.getMode(), this.swing.getValue()));
                Wrapper.swing(InteractionHand.MAIN_HAND, this.swing.getValue());
                this.timer.setLastMS();
            }

        } else {
            this.sprint = true;
            this.aimAssist = true;
        }

    }

    @Override
    public void onMotion(final MotionEvent event) {
        if (event.post) {
            return;
        }

        if (this.rotation.equals("Legal") || this.mode.equals("Legal")) {

            if (!this.isToggled() || this.legalRot == null || this.target == null || this.endRot == this.legalRot || !this.timer.isDelay(this.minCPS.getValue(), this.maxCPS.getValue()) || Wrapper.mc().hitResult instanceof EntityHitResult hitResult && hitResult.getEntity() != target) {
                return;
            }

            // Fixing GCD
            this.endRot = this.legalRot;
            VecPos target = LegalAura.patchSensitivityGCDExploit(this.legalRot);

            event.setYaw(target.yRot);
            event.setPitch(target.xRot);

            return;
        }
        if (!this.rotation.equals("None")) {
            VecPos target;
            int mode = -1;

            if (this.backRotation) {
                target = new VecPos(event.yaw, event.pitch);
                if (this.modeRot != 1 && this.modeRot != 2) {
                    mode = 1;
                }
            } else if (!this.isToggled()) {
                this.endRot = null;
                return;
            } else {
                if (this.target == null) {
                    this.endRot = null;
                    return;
                }
                target = Utils.getSimpleRotations(this.target, 1);
                if (this.modeRot != 3 && this.modeRot != 4) {
                    mode = 3;
                }
            }

            if (mode != -1) {
                this.modeRot = mode;
                this.countRot = 0;
                int executions = 11 - this.rotationSpeed.getValue();
                float stepSize1 = Math.abs(target.yRot - event.getYaw()) / executions;
                if (stepSize1 < 5) {
                    stepSize1 = 5;
                }
                float stepSize2 = Math.abs(target.xRot - event.getPitch()) / executions;
                if (stepSize2 < 5) {
                    stepSize2 = 5;
                }
                this.speedRot = new VecPos(stepSize1, stepSize2);
            }

            if (mode != -1) {
                if (!this.timer.isDelay(this.minCPS.getValue(), this.maxCPS.getValue())) {
                    if (this.endRot != target) {
                        this.endRot = target;
                        event.setYaw(target.yRot);
                        event.setPitch(target.xRot);
                        LegalAura.yHeadRot(event.getYaw());
                    }
                    return;
                }
                this.silentRotation = false;
            }

            this.countRot++;
            boolean flag = false;
            VecPos current = new VecPos(event.getYaw(), event.getPitch());
            if (target.yRot > current.yRot) {
                current.yRot += (this.speedRot.yRot * countRot);
                if (current.yRot > target.yRot) {
                    current.yRot = target.yRot;
                }
                flag = true;
            } else if (current.yRot > target.yRot) {
                current.yRot -= (this.speedRot.yRot * countRot);
                if (target.yRot > current.yRot) {
                    current.yRot = target.yRot;
                }
                flag = true;
            }

            if (target.xRot > current.xRot) {
                current.xRot += (this.speedRot.xRot * countRot);
                if (current.xRot > target.xRot) {
                    current.xRot = target.xRot;
                }
                flag = true;
            } else if (current.xRot > target.xRot) {
                current.xRot -= (this.speedRot.xRot * countRot);
                if (target.xRot > current.xRot) {
                    current.xRot = target.xRot;
                }
                flag = true;
            }

            if (flag) {
                // Fixing GCD
                if (current != this.endRot) {
                    this.endRot = current;
                    target = LegalAura.patchSensitivityGCDExploit(current);
                    event.setYaw(target.yRot);
                    event.setPitch(target.xRot);
                }
            }

            LegalAura.yHeadRot(current.yRot);
            float yawDiff = Math.abs(target.yRot - current.yRot);
            float pitchDiff = Math.abs(target.xRot - current.xRot);

            if (yawDiff <= 5.0f && pitchDiff <= 5.0f) {
                this.countRot--;
                if (this.backRotation) {
                    if (this.modeRot == 1) {
                        this.modeRot = 2;
                        this.backRotation = false;
                    }
                } else {
                    if (this.modeRot == 3) {
                        this.modeRot = 4;
                        this.silentRotation = true;
                    }
                }
            }

        }
    }

}