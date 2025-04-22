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
import space.utils.Utils;
import space.utils.Wrapper;
import space.value.Mode;
import space.value.ModeValue;

public class Flight extends Hack {
    private final ModeValue mode;

    public Flight() {
        super("Flight", HackCategory.Another);
        this.mode = new ModeValue("Mode", new Mode("Simple", true), new Mode("Jetpack"));
        this.addValue(this.mode);
    }

    @Override
    public void onAllTick() {
        switch (this.mode.getMode()) {
            case "Jetpack":
                if (Wrapper.mc().options.keyJump.isDown()) {
                    Wrapper.player().jumpFromGround();
                }
                break;
            case "Simple":
                if (!Wrapper.player().getAbilities().flying) {
                    Wrapper.player().getAbilities().flying = true;
                }
                break;
        }
    }


    @Override
    public void onDisable() {
        if (Utils.nullCheck()) {
            if (this.mode.getValue("Simple").isToggled()) {
                if (Wrapper.player().getAbilities().flying) {
                    Wrapper.player().getAbilities().flying = false;
                }
            }
        }
    }

}
