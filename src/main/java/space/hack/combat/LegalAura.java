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
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.TimerUtils;
import space.utils.Utils;
import space.utils.ValidUtils;
import space.utils.Wrapper;
import space.value.*;

public class LegalAura extends Hack {

    public final ModeValue priority;
    public final IntValue maxCPS;
    public final IntValue minCPS;
    public final IntValue hurtTime;
    public final IntValue fov;
    public final TimerUtils timer;
    public final NumberValue range;
    public final ModeValue autoBlock;
    public final BooleanValue swing;
    public final BooleanValue mouseClick;
    public final BooleanValue sprint;
    public final IntValue rotationSpeed;

    public LegalAura() {
        super("LegalAura", HackCategory.Combat);
        this.priority = ValidUtils.isPriority("Priority");
        this.maxCPS = new IntValue("MaxCPS", 11, 1, 20);
        this.minCPS = new IntValue("MinCPS", 5, 1, 19);
        this.hurtTime = new IntValue("HurtTime", 10, 0, 10);
        this.range = new NumberValue("Range", 3.8, 1.0, 6.0);
        this.swing = new BooleanValue("Swing", true);
        this.sprint = new BooleanValue("Sprint", true);
        this.autoBlock = new ModeValue("AutoBlock", new Mode("Packet", true), new Mode("UseItem", false), new Mode("Mouse"), new Mode("None"));
        this.mouseClick = new BooleanValue("MouseClick", false);
        this.fov = new IntValue("Fov", 120, 1, 360);
        this.rotationSpeed = new IntValue("RotationSpeed", 10, 1, 11);
        this.addValue(this.maxCPS, this.minCPS, this.hurtTime, this.range, this.priority, this.swing, this.sprint, this.mouseClick, this.autoBlock, this.fov, this.rotationSpeed);
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
        }

        Utils.upRotations(target, this.rotationSpeed.getValue());
        if (this.swing.getValue()){
            Wrapper.swing(InteractionHand.MAIN_HAND, target);
        }

        this.AimAssist = false;

        if(!this.sprint.getValue()) {
            this.Sprint = false;
        }

        int i1 = Utils.random(this.minCPS.getValue() ,this.maxCPS.getValue());


        if (this.timer.isDelay(i1)) {
            Utils.processAttack(target, this.autoBlock.getMode(), this.swing.getValue());
            this.timer.setLastMS();
        }
    }

}