/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.combat;

import net.minecraft.world.entity.Entity;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.Wrapper;
import space.value.IntValue;
import space.value.Mode;
import space.value.ModeValue;

public class Criticals extends Hack {
    public final ModeValue mode;
    public final IntValue ticks;
    public int ticksX = 0;

    public Criticals() {
        super("Criticals", HackCategory.Combat);
        this.mode = new ModeValue("Mode", new Mode("Visual", true),new Mode("Jump"),new Mode("LowJump"));
        this.ticks = new IntValue("Ticks", 1,0,10);
        this.addValue(this.mode, this.ticks);
    }

    @Override
    public void onAttack(final Entity event) {
        if(!Wrapper.player().isOnGround() || Wrapper.player().isInWater() || Wrapper.player().isInLava()){
            return;
        }
        ++this.ticksX;
        if (this.ticksX >= this.ticks.getValue()){
            if(this.mode.getValue("Visual").isToggled()){
                Wrapper.player().crit(event);
            } else if(this.mode.getValue("Jump").isToggled()){
                Wrapper.SetMotionY(0.42);
            } else if(this.mode.getValue("LowJump").isToggled()){
                Wrapper.SetMotionY(0.3425);
            }
            this.ticksX = 0;
        }
    }
}
