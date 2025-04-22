/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.player;

import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.AirBlock;
import space.hack.Hack;
import space.hack.HackCategory;
import space.manager.HackManager;
import space.utils.Wrapper;

public class Eagle extends Hack {

    public Eagle() {
        super("Eagle", HackCategory.Player);
    }

    public static boolean isEagle() {
        return Wrapper.level().getBlockState(new BlockPos((int) Wrapper.player().getX(), (int) (Wrapper.player().getY() - 1.0), (int) Wrapper.player().getZ())).getBlock() instanceof AirBlock;
    }

    @Override
    public void onAllTick() {
        if (onEagle()) {
            if (Wrapper.player().onGround()) {
                this.sprint = !isEagle();
                KeyMapping.set(Wrapper.mc().options.keyShift.getKey(), !this.sprint);
            }
        }
    }

    public boolean onEagle() {
        Hack hack = HackManager.getHackE("Scaffold");
        if (hack != null && hack.isToggled()) {
            return !hack.isBooleanValue("Eagle");
        }
        return true;
    }

}
