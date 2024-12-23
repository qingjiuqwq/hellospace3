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
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.Utils;
import space.utils.ValidUtils;
import space.utils.Wrapper;
import space.value.*;

public class AimAssist extends Hack {

    public final ModeValue priority;
    public final IntValue hurtTime;
    public final IntValue fOV;
    public final NumberValue range;
    public final ModeValue mode;
    public final BooleanValue mouseClick;
    public final BooleanValue sprint;
    public final IntValue rotationSpeed;

    public AimAssist() {
        super("AimAssist", HackCategory.Combat);
        this.mode = new ModeValue("Mode", new Mode("Simple", true), new Mode("Strict"));
        this.priority = ValidUtils.isPriority("Priority");
        this.hurtTime = new IntValue("HurtTime", 10, 0, 10);
        this.range = new NumberValue("Range", 3.8, 1.0, 6.0);
        this.sprint = new BooleanValue("Sprint", true);
        this.mouseClick = new BooleanValue("MouseClick", false);
        this.fOV = new IntValue("FOV", 120, 1, 360);
        this.rotationSpeed = new IntValue("RotationSpeed", 10, 1, 11);
        this.addValue(this.mode, this.hurtTime, this.range, this.priority, this.sprint, this.mouseClick, this.fOV, this.rotationSpeed);
    }

    @Override
    public void onAllTick() {
        if (this.mouseClick.getValue() && !(Wrapper.mc().options.keyAttack.isDown() || Wrapper.mc().options.keyUse.isDown())) {
            this.Sprint = true;
            this.AimAssist = true;
            return;
        }

        LivingEntity target = null;
        if (Wrapper.mc().hitResult instanceof EntityHitResult hitResult) {
            if (hitResult.getEntity() instanceof LivingEntity livingEntity) {
                if(ValidUtils.check(livingEntity, this.fOV.getValue(), this.range.getValue(), this.hurtTime.getValue()) == 0){
                    target = livingEntity;
                }
            }
        }

        if (target == null) {
            this.Sprint = true;
            this.AimAssist = true;
            target = ValidUtils.SimpleUpdate(this.fOV.getValue(), this.range.getValue(), this.priority.getMode(), this.hurtTime.getValue());
            if (target != null) {
                Utils.upRotations(target, this.rotationSpeed.getValue());
            }
        }else if (this.mode.equals("Simple")) {
            Utils.upRotations(target, this.rotationSpeed.getValue());
        }

        this.AimAssist = false;
        if(!this.sprint.getValue()) {
            this.Sprint = false;
        }

    }
}