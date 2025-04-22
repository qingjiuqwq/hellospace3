/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.combat;

import net.minecraft.client.CameraType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import space.hack.Hack;
import space.hack.HackCategory;
import space.manager.ClassManager;
import space.utils.*;
import space.utils.vec.Vec4;
import space.utils.vec.VecPos2;
import space.utils.vec.rot.LegalPacket;
import space.utils.vec.rot.LegalRotation;
import space.utils.vec.rot.RotationMode;
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
    private final RotationMode rotationMode;
    public LegalPacket legalPacket;
    private LivingEntity target;
    private LivingEntity legalTarget;

    public KillAura() {
        super("KillAura", HackCategory.Combat, true);
        this.mode = new ModeValue("Mode", new Mode("Simple", true), new Mode("Legal"), new Mode("Strict"));
        this.priority = ValidUtils.isPriority("Priority");
        this.maxCPS = new IntValue("MaxCPS", 11, 1, 20);
        this.minCPS = new IntValue("MinCPS", 5, 1, 19);
        this.hurtTime = new IntValue("HurtTime", 10, 0, 10);
        this.range = new NumberValue("Range", 3.8, 1.0, 6.0);
        this.aac = new BooleanValue("AAC", false);
        this.swing = new BooleanValue("Swing", true);
        this.sprintMode = new BooleanValue("Sprint", true);
        this.rotation = new ModeValue("Rotation", new Mode("Silent", true), new Mode("Legal"), new Mode("None"));
        this.autoBlock = new ModeValue("AutoBlock", new Mode("Packet", true), new Mode("UseItem", false), new Mode("Mouse"), new Mode("None"));
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
        this.timer = new TimerUtils();
        this.legalPacket = new LegalPacket();
        this.rotationMode = new RotationMode();
    }

    @Override
    public void onEnable() {
        this.target = null;
        this.clear(true);
    }

    @Override
    public void onAllTick() {
        if ((this.aac.getValue() && Wrapper.mc().screen != null) ||
                (this.mouseClick.getValue() && !(Wrapper.mc().options.keyAttack.isDown() || Wrapper.mc().options.keyUse.isDown()))) {
            this.onEnable();
            return;
        }
        this.sprint = true;
        this.aimAssist = true;
        LivingEntity target = Utils.getTarget(this);
        if (target != null) {
            this.target = target;
        } else {
            this.target = ValidUtils.SimpleUpdate(this.fov.getValue(), this.range.getValue(), this.priority.getMode(), this.hurtTime.getValue());
        }
        if (this.target == null) {
            this.clear();
            return;
        }
        if (this.rotation.equals("Silent") && !this.mode.equals("Legal")) {
            if (this.rotationMode.targetEq(this.target)) {
                this.clear();
                this.rotationMode.target(this.target);
            }
            if (Wrapper.isKeyMove() && this.rotationMode.sendPacket() && this.target != this.legalTarget && !this.rotationMode.success) {
                VecPos2 pos = Utils.updateRotation(this.rotationMode.pos, this.rotationMode.current, this.rotationSpeed.getValue());
                this.rotationMode.pos = pos;
                Packet<?> packet = new ServerboundMovePlayerPacket.Rot(pos.yRot, pos.xRot, Wrapper.player().onGround());
                LegalRotation legalRotation = new LegalRotation(packet, pos, pos.success ? this.target : null);
                this.rotationMode.add(legalRotation);
                Wrapper.sendPacket(packet);
            }
        } else {
            if (!this.mode.equals("Legal") || !this.rotation.equals("None")) {
                Utils.upRotations(this.target, this.rotationSpeed.getValue());
            }
        }
        if (this.mode.equals("Strict") && this.rotation.equals("Silent") && (this.target != this.legalTarget || !this.rotationMode.success)) {
            this.yHeadRot(this.target);
            return;
        }
        this.yHeadRot(this.target);
        this.onTick(this.target);
    }

    public void clear(boolean current) {
        this.sprint = true;
        this.aimAssist = true;
        this.legalTarget = null;
        this.legalPacket.clear();
        this.rotationMode.clear(current);
    }

    public void clear() {
        this.clear(false);
    }

    public void yHeadRot(LivingEntity target) {
        if (Wrapper.mc().options.getCameraType() != CameraType.FIRST_PERSON) {
            Wrapper.player().setYHeadRot(Utils.getSimpleRotations(target).yRot);
        }
    }

    public void onTick(LivingEntity target) {
        if (target == null) {
            return;
        }

        this.aimAssist = false;

        if (!this.sprintMode.getValue()) {
            this.sprint = false;
        }

        if (this.timer.isDelay(this.minCPS.getValue(), this.maxCPS.getValue())) {
            if (this.rotation.equals("Legal") || this.mode.equals("Legal")) {
                if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult && hitResult.getEntity() == target) {
                    this.legalPacket.add(Utils.processAttack(target, this.autoBlock.getMode(), this.swing.getValue()));
                    Wrapper.swing(InteractionHand.MAIN_HAND, this.swing.getValue());
                }
            } else {
                this.legalPacket.add(Utils.processAttack(target, this.autoBlock.getMode(), this.swing.getValue()));
                Wrapper.swing(InteractionHand.MAIN_HAND, this.swing.getValue());
            }
            this.timer.setLastMS();
        }
    }

    @Override
    public synchronized int onPacket(final Object packet, final Connection.Side side) {
        if (this.rotation.equals("Silent") && Connection.Side.OUT == side) {
            for (int i = 0; i < this.rotationMode.size(); i++) {
                LegalRotation legalRotation = this.rotationMode.get(i);
                if (legalRotation != null && legalRotation.packet == packet) {
                    this.legalTarget = legalRotation.target;
                    this.rotationMode.remove(i);
                    this.rotationMode.current = legalRotation.pos;
                    return legalRotation.target != null ? 2 : 1;
                }
            }
            for (int i = 0; i < this.legalPacket.size(); i++) {
                if (this.legalPacket.get(i) == packet) {
                    this.legalPacket.remove(i);
                    return super.onPacket(packet, side);
                }
            }
            if (this.target != null && (packet instanceof ServerboundMovePlayerPacket.Rot || packet instanceof ServerboundInteractPacket || packet instanceof ServerboundPlayerActionPacket)) {
                return 0;
            }
            if (packet instanceof ServerboundMovePlayerPacket.Pos || packet instanceof ServerboundMovePlayerPacket.PosRot) {
                Vec4 vec4 = new Vec4(packet);
                if (!vec4.success()) {
                    ChatUtils.error("Rotation Error");
                    return super.onPacket(packet, side);
                }
                if (packet instanceof ServerboundMovePlayerPacket.Pos && this.rotationMode.xyzEq(vec4)) {
                    this.rotationMode.setLastMS();
                    return super.onPacket(packet, side);
                }
                LivingEntity target = this.target;
                if (target == null || this.rotationMode.targetEq(target, vec4)) {
                    this.rotationMode.xyz(vec4);
                    return super.onPacket(packet, side);
                }
                this.clear();
                this.rotationMode.target(target, vec4);
                if (packet instanceof ServerboundMovePlayerPacket.Pos) {
                    Wrapper.sendPacket(new ServerboundMovePlayerPacket.PosRot(vec4.x, vec4.y, vec4.z, this.rotationMode.pos.yRot, this.rotationMode.pos.xRot, vec4.onGround));
                    return 0;
                } else {
                    this.legalTarget = target;
                    this.rotationMode.current = this.rotationMode.pos;
                    ReflectionHelper.setPrivateValue(packet, this.rotationMode.pos.yRot, ClassManager.serverboundMovePlayerPacket_yRot);
                    ReflectionHelper.setPrivateValue(packet, this.rotationMode.pos.xRot, ClassManager.serverboundMovePlayerPacket_xRot);
                }
                this.rotationMode.xyz(vec4);
                return 2;
            }
        }
        return super.onPacket(packet, side);
    }


    @Override
    public void onPFixes(final Object packet, final Connection.Side side, final boolean send) {
        this.rotationMode.success = true;
    }

}