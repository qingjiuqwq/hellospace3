/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.another;

import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.Wrapper;
import space.value.Mode;
import space.value.ModeValue;

import java.util.Objects;

public class Speed extends Hack {
    public final ModeValue mode;

    public Speed() {
        super("Speed", HackCategory.Another);
        this.mode = new ModeValue("Mode", new Mode("HYT", true), new Mode("Jumping"));
        this.addValue(this.mode);
    }

    @Override
    public void onAllTick() {
        if (this.mode.getValue("HYT").isToggled() || this.mode.getValue("Jumping").isToggled()) {
            if (Wrapper.player().input.forwardImpulse > 0 && Wrapper.player().isOnGround()) {
                Objects.requireNonNull(Wrapper.mc().player).jumpFromGround();
                if (this.mode.getValue("Jumping").isToggled()){
                    Wrapper.mc().player.jumpFromGround();
                    Wrapper.mc().player.jumpFromGround();
                    Wrapper.mc().player.jumpFromGround();
                    Wrapper.mc().player.jumpFromGround();
                }
            }
        }
    }
}