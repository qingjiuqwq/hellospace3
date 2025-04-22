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

public class LongJump extends Hack {
    private final ModeValue mode;
    public int jumpTick = 0;
    public boolean ground = false;

    public LongJump() {
        super("LongJump", HackCategory.Another);
        this.mode = new ModeValue("Mode", new Mode("9JUMP", true), new Mode("12JUMP"), new Mode("15JUMP"), new Mode("17JUMP"), new Mode("25JUMP"));
        this.addValue(this.mode);
    }

    @Override
    public void onEnable() {
        this.jumpTick = 0;
        this.ground = false;
        this.sprint = true;
    }

    public int JUMP() {
        if (this.mode.getValue("9JUMP").isToggled()) {
            return 9;
        } else if (this.mode.getValue("12JUMP").isToggled()) {
            return 12;
        } else if (this.mode.getValue("15JUMP").isToggled()) {
            return 15;
        } else if (this.mode.getValue("17JUMP").isToggled()) {
            return 17;
        } else if (this.mode.getValue("25JUMP").isToggled()) {
            return 25;
        }
        return 0;
    }

    @Override
    public void onAllTick() {
        if (this.ground) {
            if (jumpTick > JUMP()) {
                this.jumpTick = 0;
                this.ground = false;
                this.sprint = true;
            } else {
                this.sprint = false;
                Objects.requireNonNull(Wrapper.mc().player).jumpFromGround();
                jumpTick++;
            }
        } else if (Wrapper.mc().options.keyJump.isDown()) {
            this.ground = true;
        }
    }
}