/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.combat;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import space.hack.Hack;
import space.hack.HackCategory;
import space.manager.ClassManager;
import space.utils.*;
import space.utils.vec.VecPos;
import space.value.*;

public class AimAssist extends Hack {
    private final ModeValue priority;
    private final IntValue hurtTime;
    private final IntValue fov;
    private final NumberValue range;
    private final BooleanValue aac;
    private final BooleanValue mouseClick;
    private final BooleanValue sprintMode;
    private final IntValue rotationSpeed;
    private LivingEntity target;
    private VecPos legalRot;

    public AimAssist() {
        super("AimAssist", HackCategory.Combat);
        this.priority = ValidUtils.isPriority("Priority");
        this.hurtTime = new IntValue("HurtTime", 10, 0, 10);
        this.range = new NumberValue("Range", 3.8, 1.0, 6.0);
        this.aac = new BooleanValue("AAC", false);
        this.sprintMode = new BooleanValue("Sprint", true);
        this.mouseClick = new BooleanValue("MouseClick", false);
        this.fov = new IntValue("Fov", 120, 1, 360);
        this.rotationSpeed = new IntValue("RSilentSpeed", 10, 1, 10);
        this.addValue(
                this.hurtTime, this.range,
                new TextValue(28),
                this.priority, this.aac, this.sprintMode, this.mouseClick,
                new TextValue(28),
                this.fov, this.rotationSpeed,
                new TextValue(28)
        );
    }

    @Override
    public void onEnable() {
        this.target = null;
        this.legalRot = null;
    }

    @Override
    public void onAllTick() {
        if ((this.aac.getValue() && Wrapper.mc().screen != null) ||
                (this.mouseClick.getValue() && !(Wrapper.mc().options.keyAttack.isDown() || Wrapper.mc().options.keyUse.isDown()))) {
            this.onEnable();
            return;
        }

        LivingEntity target = Utils.getTarget(this);
        if (target == null) {
            target = ValidUtils.SimpleUpdate(this.fov.getValue(), this.range.getValue(), this.priority.getMode(), this.hurtTime.getValue());
        }

        if (target == null || !target.equals(this.target)) {
            this.target = target;
        }

        if (target == null) {
            return;
        }

        this.legalRot = Utils.upRotations(target, this.rotationSpeed.getValue());

        if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult && hitResult.getEntity().equals(target)) {
            this.aimAssist = false;

            if (!this.sprintMode.getValue()) {
                this.sprint = false;
            }

        } else {
            this.sprint = true;
            this.aimAssist = true;
        }

    }

    @Override
    public int onPacket(final Object packet, final Connection.Side side) {
        if (Connection.Side.OUT != side) {
            return super.onPacket(packet, side);
        }
        if (this.legalRot == null) {
            return super.onPacket(packet, side);
        }
        if (packet instanceof ServerboundMovePlayerPacket.PosRot || packet instanceof ServerboundMovePlayerPacket.Rot) {
            VecPos vecPos = VecPos.getVecPos(packet);
            if (vecPos == null) {
                return super.onPacket(packet, side);
            }
            if (vecPos.yRot == this.legalRot.yRot) {
                // Fixing GCD
                vecPos = LegalAura.patchSensitivityGCDExploit(this.legalRot);
                ReflectionHelper.setPrivateValue(packet, vecPos.yRot, ClassManager.serverboundMovePlayerPacket_yRot);
                ReflectionHelper.setPrivateValue(packet, vecPos.xRot, ClassManager.serverboundMovePlayerPacket_xRot);
            }
        }
        return super.onPacket(packet, side);
    }

}