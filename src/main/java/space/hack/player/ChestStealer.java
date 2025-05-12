/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.player;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.TimerUtils;
import space.utils.Wrapper;
import space.value.BooleanValue;
import space.value.IntValue;

public class ChestStealer extends Hack {
    public final TimerUtils speedPacket;
    private final IntValue maxCPS;
    private final IntValue minCPS;
    private final BooleanValue autoToolMode;
    private final BooleanValue autoClose;

    public ChestStealer() {
        super("ChestStealer", HackCategory.Player);
        this.maxCPS = new IntValue("MaxDelay", 200, 1, 400);
        this.minCPS = new IntValue("MinDelay", 150, 1, 390);
        this.autoToolMode = new BooleanValue("AutoTool", false);
        this.autoClose = new BooleanValue("AutoClose", false);
        this.addValue(this.maxCPS, this.minCPS, this.autoToolMode, this.autoClose);
        this.speedPacket = new TimerUtils();
    }

    @Override
    public void onAllTick() {
        AbstractContainerMenu containerMenu = Wrapper.player().containerMenu;
        if (containerMenu instanceof ChestMenu chestMenu) {
            Container container = chestMenu.getContainer();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack item = container.getItem(i);
                if (!item.isEmpty() && this.noContinue(item, container)) {
                    this.handleInventory(chestMenu, i);
                }
            }
            if (this.autoClose.getValue() && this.isContainerEmpty(container)) {
                Wrapper.player().closeContainer();
            }
        }
    }

    public boolean isContainerEmpty(final Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack item = container.getItem(i);
            if (!item.isEmpty() && this.noContinue(item, container)) {
                return false;
            }
        }
        return true;
    }

    public boolean noContinue(final ItemStack stack, final Container container) {
        return !this.autoToolMode.getValue() || !this.equalsBest(stack, container);
    }

    public void handleInventory(final ChestMenu container, final int index) {
        if (!(this.speedPacket.isDelayX(this.minCPS, this.maxCPS)) || Wrapper.mc().gameMode == null) {
            return;
        }
        this.speedPacket.setLastMS();
        Wrapper.mc().gameMode.handleInventoryMouseClick(container.containerId, index, 0, ClickType.QUICK_MOVE, Wrapper.player());
    }

    public boolean equalsBest(final ItemStack stack, final Container container) {
        int isTool = AutoTool.isTool(stack);
        if (isTool == -1) {
            return false;
        }
        double maxDamage = -1;
        for (int i = 0; i < Wrapper.player().getInventory().items.size(); ++i) {
            ItemStack itemStack = Wrapper.player().getInventory().getItem(i);
            if (AutoTool.isTool(itemStack) == isTool) {
                double damage = (isTool == 0 ? AutoTool.damage(itemStack) : AutoTool.damageTool(itemStack));
                if (damage > maxDamage) {
                    maxDamage = damage;
                }
            }
        }
        if (maxDamage == -1) {
            return false;
        }
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (AutoTool.isTool(itemStack) == isTool && itemStack != stack) {
                double damage = (isTool == 0 ? AutoTool.damage(itemStack) : AutoTool.damageTool(itemStack));
                if (damage > maxDamage) {
                    maxDamage = damage;
                }
            }
        }
        return (isTool == 0 ? AutoTool.damage(stack) : AutoTool.damageTool(stack)) < maxDamage;
    }

}