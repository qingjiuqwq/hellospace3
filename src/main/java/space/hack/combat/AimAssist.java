/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import space.Core;
import space.hack.Hack;
import space.hack.HackCategory;
import space.manager.HackManager;
import space.utils.Utils;
import space.utils.ValidUtils;
import space.utils.Wrapper;
import space.value.*;

public class AimAssist extends Hack {

    private final ModeValue priority;
    private final IntValue hurtTime;
    private final IntValue fOV;
    private final NumberValue range;
    private final ModeValue mode;
    private final BooleanValue mouseClick;
    private final BooleanValue sprintMode;
    private final IntValue rotationSpeed;
    private final BooleanValue rotationMode;
    private final IntValue deltaYMode;

    public AimAssist() {
        super("AimAssist", HackCategory.Combat);
        this.mode = new ModeValue("Mode", new Mode("Simple", true), new Mode("Strict"));
        this.priority = ValidUtils.isPriority("Priority");
        this.hurtTime = new IntValue("HurtTime", 10, 0, 10);
        this.range = new NumberValue("Range", 3.8, 1.0, 6.0);
        this.deltaYMode = new IntValue("DeltaYMode", 0, 0, 2);
        this.sprintMode = new BooleanValue("Sprint", true);
        this.mouseClick = new BooleanValue("MouseClick", false);
        this.rotationMode = new BooleanValue("RotationMode", false);
        this.fOV = new IntValue("FOV", 120, 1, 360);
        this.rotationSpeed = new IntValue("RotationSpeed", 10, 1, 10);
        this.addValue(this.mode, this.hurtTime, this.range, this.priority, this.sprintMode, this.mouseClick, this.rotationMode, this.fOV, this.rotationSpeed);
        if (Core.mode) {
            this.addValue(this.deltaYMode);
        }
    }

    @Override
    public void onAllTick() {
        boolean mouse = this.mouseClick.getValue() && !(Wrapper.mc().options.keyAttack.isDown() || Wrapper.mc().options.keyUse.isDown());
        if (HackManager.noAimAssist() || (this.rotationMode.getValue() && mouse) || mouse) {
            return;
        }
        this.onTick();
    }

    public void onTick() {
        LivingEntity target = null;
        if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult) {
            if (hitResult.getEntity() instanceof LivingEntity livingEntity) {
                if (ValidUtils.check(livingEntity, this.fOV.getValue(), this.range.getValue(), this.hurtTime.getValue()) == 0) {
                    target = livingEntity;
                }
            }
        }

        if (target == null) {
            this.sprint = true;
            target = ValidUtils.SimpleUpdate(this.fOV.getValue(), this.range.getValue(), this.priority.getMode(), this.hurtTime.getValue());
            if (target != null) {
                Utils.upRotations(target, this.rotationSpeed.getValue(), this.deltaYMode.getValue());
            }
        } else if (this.mode.equals("Simple")) {
            Utils.upRotations(target, this.rotationSpeed.getValue(), this.deltaYMode.getValue());
        }

        if (!this.sprintMode.getValue()) {
            this.sprint = false;
        }
    }
}