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
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.Wrapper;
import space.value.BooleanValue;
import space.value.Mode;
import space.value.ModeValue;

public class Scaffold extends Hack
{
    public final ModeValue autoBlock;
    public final BooleanValue eagle;
    public final BooleanValue swing;
    public final BooleanValue mouseDisabled;

    public Scaffold() {
        super("Scaffold", HackCategory.Player);
        this.autoBlock = new ModeValue("AutoBlock", new Mode("Keep", true), new Mode("Spoof"), new Mode("None"));
        this.eagle = new BooleanValue("Eagle" , false);
        this.swing = new BooleanValue("Swing", true);
        this.mouseDisabled = new BooleanValue("MouseDisabled", false);
        this.addValue(this.autoBlock, this.eagle, this.swing, this.mouseDisabled);
    }

    @Override
    public void onAllTick() {
        if (this.eagle.getValue()){
            if (Wrapper.player().isOnGround()){
                this.Sprint = Eagle.isEagle();
                KeyMapping.set(Wrapper.mc().options.keyShift.getKey(), !this.Sprint);
            }
        }

        if (this.mouseDisabled.getValue()){
            if (Wrapper.mc().options.keyAttack.isDown() || Wrapper.mc().options.keyUse.isDown()){
                return;
            }
        }

        Vec3 playerPos = Wrapper.player().position();

        BlockPos blockPos = new BlockPos(playerPos.x, playerPos.y - 1, playerPos.z);

        BlockState blockState  = Wrapper.level().getBlockState(blockPos);

        if (!blockState.isAir()){
            this.AutoTool = true;
            return;
        }

        this.AutoTool = false;

        ItemStack itemInHand = Wrapper.player().getMainHandItem();
        if (itemInHand.getItem() instanceof BlockItem) {
            this.Swing();

            BlockHitResult hitResult = new BlockHitResult(playerPos, Direction.UP, blockPos, false);
            Wrapper.sendPacket(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hitResult));

        }else if (this.autoBlock.equals("Keep")){
            ItemStack block = findSlotWithBlockB();
            if (block != null){
                int selected = Wrapper.player().getInventory().selected;
                Wrapper.player().getInventory().setPickedItem(block);

                this.Swing();
                BlockHitResult hitResult = new BlockHitResult(playerPos, Direction.UP, blockPos, false);
                Wrapper.sendPacket(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hitResult));

                Wrapper.player().getInventory().selected = selected;
            }
        }else if (this.autoBlock.equals("Spoof")){
            ItemStack block = findSlotWithBlockB();
            if (block != null){
                Wrapper.player().getInventory().setPickedItem(block);

                this.Swing();
                BlockHitResult hitResult = new BlockHitResult(playerPos, Direction.UP, blockPos, false);
                Wrapper.sendPacket(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hitResult));
            }
        }
    }

    public void Swing(){
        if (this.swing.getValue()){
            Wrapper.swing(InteractionHand.MAIN_HAND);
        }
    }

    public static ItemStack findSlotWithBlockB() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Wrapper.player().getInventory().getItem(i);
            if (stack.getItem() instanceof BlockItem) {
                return stack;
            }
        }
        return null;
    }

}