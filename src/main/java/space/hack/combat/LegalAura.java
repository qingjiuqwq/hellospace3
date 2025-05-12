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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import space.hack.Hack;
import space.hack.HackCategory;
import space.manager.ClassManager;
import space.utils.*;
import space.utils.vec.VecPos;
import space.value.*;

public class LegalAura extends Hack {
    private final ModeValue mode;
    private final ModeValue priority;
    private final IntValue maxCPS;
    private final IntValue minCPS;
    private final IntValue hurtTime;
    private final IntValue fov;
    private final TimerUtils timer;
    private final NumberValue range;
    private final ModeValue autoBlock;
    private final BooleanValue aac;
    private final BooleanValue swing;
    private final BooleanValue mouseClick;
    private final BooleanValue sprintMode;
    private final ModeValue rotation;
    private final IntValue rotationSpeed;
    private LivingEntity target;
    private VecPos legalRot;

    public LegalAura() {
        super("LegalAura", HackCategory.Combat, true);
        this.mode = new ModeValue("Mode", "Simple", "HVH");
        this.priority = ValidUtils.isPriority("Priority");
        this.maxCPS = new IntValue("MaxCPS", 11, 1, 20);
        this.minCPS = new IntValue("MinCPS", 5, 1, 19);
        this.hurtTime = new IntValue("HurtTime", 10, 0, 10);
        this.range = new NumberValue("Range", 3.8, 1.0, 6.0);
        this.aac = new BooleanValue("AAC", false);
        this.swing = new BooleanValue("Swing", true);
        this.sprintMode = new BooleanValue("Sprint", true);
        this.rotation = new ModeValue("Rotation", "Legal", "None");
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
        this.timer = new TimerUtils();
    }

    /*
     * Fixing GCD
     */
    public static VecPos patchSensitivityGCDExploit(VecPos target) {
        float yRotLast = Wrapper.getYRotLast();
        float xRotLast = Wrapper.getXRotLast();

        double gcd = Math.pow((Wrapper.mc().options.sensitivity().get() * 0.6F + 0.2F), 3) * 1.2;
        double yaw = yRotLast + Math.round((target.yRot - yRotLast) / gcd) * gcd;
        double pitch = xRotLast + Math.round((target.xRot - xRotLast) / gcd) * gcd;

        return new VecPos((float) yaw, (float) pitch);
    }

    public static void yHeadRot(Entity entity) {
        yHeadRot(Utils.getSimpleRotations(entity, 1).yRot);
    }

    public static void yHeadRot(float yRot) {
        if (Wrapper.mc().options.getCameraType() != CameraType.FIRST_PERSON) {
            Wrapper.player().setYHeadRot(yRot);
        }
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

        boolean flag = false;
        if (this.mode.equals("HVH")) {
            flag = true;
        } else if (this.mode.equals("Simple")) {
            flag = Wrapper.mc().hitResult instanceof EntityHitResult hitResult && hitResult.getEntity().equals(target);
        }

        if (this.rotation.equals("Legal")) {
            this.legalRot = Utils.upRotations(target, this.rotationSpeed.getValue());
        } else if (this.rotation.equals("None")) {
            yHeadRot(target);
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
    public int onPacket(final Object packet, final Connection.Side side) {
        if (Connection.Side.OUT != side) {
            return super.onPacket(packet, side);
        }
        if (this.rotation.equals("Legal")) {
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
                    vecPos = patchSensitivityGCDExploit(this.legalRot);
                    ReflectionHelper.setPrivateValue(packet, vecPos.yRot, ClassManager.serverboundMovePlayerPacket_yRot);
                    ReflectionHelper.setPrivateValue(packet, vecPos.xRot, ClassManager.serverboundMovePlayerPacket_xRot);
                }
            }
        }
        return super.onPacket(packet, side);
    }

}