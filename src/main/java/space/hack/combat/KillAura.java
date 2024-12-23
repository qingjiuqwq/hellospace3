/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.combat;

import net.minecraft.client.CameraType;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.*;
import space.value.*;

public class KillAura extends Hack {

    public final ModeValue priority;
    public final IntValue maxCPS;
    public final IntValue minCPS;
    public final IntValue hurtTime;
    public final IntValue fov;
    public final TimerUtils timer;
    public final NumberValue range;
    public final ModeValue mode;
    public final ModeValue autoBlock;
    public final BooleanValue swing;
    public final BooleanValue mouseClick;
    public final BooleanValue sprint;
    public final BooleanValue silentRotation;
    public final IntValue rotationSpeed;
    public float[] rotations = null;

    public KillAura() {
        super("KillAura", HackCategory.Combat);
        this.mode = new ModeValue("Mode", new Mode("Simple", true));
        this.priority = ValidUtils.isPriority("Priority");
        this.maxCPS = new IntValue("MaxCPS", 11, 1, 20);
        this.minCPS = new IntValue("MinCPS", 5, 1, 19);
        this.hurtTime = new IntValue("HurtTime", 10, 0, 10);
        this.range = new NumberValue("Range", 3.8, 1.0, 6.0);
        this.swing = new BooleanValue("Swing", true);
        this.sprint = new BooleanValue("Sprint", true);
        this.silentRotation = new BooleanValue("SilentRotation", true);
        this.autoBlock = new ModeValue("AutoBlock", new Mode("UseItem", true), new Mode("Packet", false), new Mode("Mouse"), new Mode("None"));
        this.mouseClick = new BooleanValue("MouseClick", false);
        this.fov = new IntValue("Fov", 120, 1, 360);
        this.rotationSpeed = new IntValue("RSilentSpeed", 10, 1, 11);
        this.addValue(this.mode, this.maxCPS, this.minCPS, this.hurtTime, this.range, this.priority, this.swing, this.sprint, this.mouseClick, this.silentRotation, this.autoBlock, this.fov, this.rotationSpeed);
        this.timer = new TimerUtils();
    }

    @Override
    public void onAllTick() {
        LivingEntity target = Utils.getTarget(this);
        if (target == null) {
            this.Sprint = true;
            this.AimAssist = true;
            target = ValidUtils.SimpleUpdate(this.fov.getValue(), this.range.getValue(), this.priority.getMode(), this.hurtTime.getValue());
            if (target == null) {
                return;
            }
            if (this.silentRotation.getValue()) {
                this.rotations = Utils.getSimpleRotations(target);
            } else {
                Utils.upRotations(target, this.rotationSpeed.getValue());
            }
        }

        this.AimAssist = false;

        if (Wrapper.mc().options.getCameraType() != CameraType.FIRST_PERSON){
            Wrapper.player().setYHeadRot(Utils.getSimpleRotations(target)[0]);
        }

        if(!this.sprint.getValue()) {
            this.Sprint = false;
        }

        int i1 = Utils.random(this.minCPS.getValue() ,this.maxCPS.getValue());

        if (this.timer.isDelay(i1)) {
            if (!this.silentRotation.getValue()) {
                if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult) {
                    if (hitResult.getEntity() == target) {
                        Utils.processAttack(target, this.autoBlock.getMode(), this.swing.getValue());
                    }
                }
            }else{
                Utils.processAttack(target, this.autoBlock.getMode(), this.swing.getValue());
            }
            this.timer.setLastMS();
        }
    }

    @Override
    public boolean onPacket(final Object packet, final Connection.Side side) {
        if (this.rotations != null && Connection.Side.OUT == side && packet instanceof ServerboundMovePlayerPacket.Rot packetPlayer) {
            ReflectionHelper.setPrivateValue(ServerboundMovePlayerPacket.Rot.class, packetPlayer, this.rotations[0], "yRot");
            ReflectionHelper.setPrivateValue(ServerboundMovePlayerPacket.Rot.class, packetPlayer, this.rotations[1], "xRot");
            this.rotations = null;
        }
        return super.onPacket(packet, side);
    }
}