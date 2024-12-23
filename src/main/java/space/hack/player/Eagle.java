/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.player;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import space.hack.Hack;
import space.hack.HackCategory;
import space.manager.HackManager;
import space.utils.Wrapper;

public class Eagle extends Hack
{

    public Eagle() {
        super("Eagle", HackCategory.Player);
    }

    @Override
    public void onAllTick() {
        if (onEagle()) {
            if (Wrapper.player().isOnGround()){
                this.Sprint = isEagle();
                KeyMapping.set(Wrapper.mc().options.keyShift.getKey(), !this.Sprint);
            }
        }
    }

    public boolean onEagle() {
        Hack hack = HackManager.getHackE("Scaffold");
        if (hack != null && hack.isToggled()){
            return !hack.isBooleanValue("Eagle");
        }
        return true;
    }

    public static boolean isEagle() {
        return !(getBlockUnderPlayer(Wrapper.player()) instanceof AirBlock) || !Wrapper.player().isOnGround() || !(LookVecY() < -0.6660000085830688);
    }

    public static Block getBlock(final BlockPos pos) {
        return Wrapper.level().getBlockState(pos).getBlock();
    }

    public static Block getBlockUnderPlayer(final LocalPlayer player) {
        return getBlock(new BlockPos(player.xo, player.yo - 1.0, player.zo));
    }

    public static double LookVecY(){
        return Wrapper.player().getLookAngle().y;
    }

}
