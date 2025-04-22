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
import space.manager.HackManager;
import space.utils.Wrapper;
import space.value.Mode;
import space.value.ModeValue;

public class Sprint extends Hack {
    private final ModeValue mode;

    public Sprint() {
        super("Sprint", HackCategory.Another);
        this.mode = new ModeValue("Priority", new Mode("KeepSprint", true), new Mode("Strict"));
        this.addValue(this.mode);
    }

    @Override
    public void onAllTick() {
        if (this.onSprint() && !Wrapper.player().isSprinting()) {
            if (this.mode.getValue("Strict").isToggled()) {
                if (Wrapper.mc().options.keyShift.isDown() || Wrapper.player().getFoodData().getFoodLevel() <= 6 ||
                        Wrapper.player().input.forwardImpulse <= 0 || Wrapper.player().horizontalCollision ||
                        Wrapper.player().isUsingItem()) return;
            }
            Wrapper.player().setSprinting(true);
        }
    }

    public boolean onSprint() {
        for (final Hack hack : HackManager.getHack()) {
            if (!hack.sprint && hack.isToggled()) {
                return false;
            }
        }
        return true;
    }
}
