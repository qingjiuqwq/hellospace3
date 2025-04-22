/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.player;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.ReflectionHelper;
import space.utils.Wrapper;
import space.value.BooleanValue;
import space.value.IntValue;

public class FastPlace extends Hack {
    private final IntValue delaySlider;
    private final BooleanValue OnlyBox;

    public FastPlace() {
        super("FastPlace", HackCategory.Player);
        this.delaySlider = new IntValue("delaySlider", 1, 1, 4);
        this.OnlyBox = new BooleanValue("OnlyBox", false);
        this.addValue(this.delaySlider, this.OnlyBox);
    }

    public static boolean isHoldingBlock() {
        ItemStack heldItem = Wrapper.player().getMainHandItem();
        if (heldItem.isEmpty()) {
            return false;
        }
        Item heldItemBlock = heldItem.getItem();
        int heldItemId = Item.getId(heldItemBlock);
        return heldItemId >= 1 && heldItemId <= 186;
    }

    @Override
    public void onAllTick() {
        if (OnlyBox.getValue() && !isHoldingBlock()) {
            return;
        }
        ReflectionHelper.setPrivateValue(Wrapper.mc(), this.delaySlider.getValue() - 1, "rightClickDelay", "f_91011_");
    }

}