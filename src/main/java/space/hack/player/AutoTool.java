/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.player;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import space.hack.Hack;
import space.hack.HackCategory;
import space.manager.HackManager;
import space.utils.Wrapper;
import space.value.Mode;
import space.value.ModeValue;

public class AutoTool extends Hack {
    private final ModeValue priority;

    public AutoTool() {
        super("AutoTool", HackCategory.Player);
        this.priority = new ModeValue("Priority", new Mode("All", true), new Mode("Weapon"), new Mode("Tool"));
        this.addValue(this.priority);
    }

    public static int isTool(final Item item) {
        if (item instanceof SwordItem) {
            return 0;
        }
        if (item instanceof PickaxeItem) {
            return 1;
        }
        if (item instanceof AxeItem) {
            return 2;
        }
        if (item instanceof ShovelItem) {
            return 3;
        }
        if (item instanceof HoeItem) {
            return 4;
        }
        return -1;
    }

    public static int isTool(final ItemStack stack) {
        return isTool(stack.getItem());
    }

    public static double damage(final ItemStack stack) {
        return ((SwordItem) stack.getItem()).getDamage() + EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED) + 1.0 + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, stack) * 0.6;
    }

    public static double damageTool(final ItemStack stack) {
        return damageTool(stack, true);
    }

    public static double damageTool(final ItemStack stack, final boolean tier) {
        DiggerItem tool = (DiggerItem) stack.getItem();
        int eff = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);

        double baseSpeed = tier ? tool.getTier().getSpeed() : 0;
        double efficiencyBonus = (eff > 0) ? (Math.pow(eff, 2.0) + 1.0) : 0.0;

        return baseSpeed + efficiencyBonus;
    }

    public static void equip(final int slot) {
        Wrapper.player().getInventory().selected = slot;
    }

    @Override
    public void onLeftClickBlock(final BlockPos event) {
        if (onAutoTool() && !this.priority.getValue("Weapon").isToggled()) {
            this.equipBestTool(Wrapper.level().getBlockState(event));
        }
    }

    @Override
    public void onAttack(final Entity event) {
        if (onAutoTool() && !this.priority.getValue("Tool").isToggled()) {
            this.equipBestWeapon();
        }
    }

    public void equipBestWeapon() {
        int bestSlot = -1;
        double maxDamage = 0.0;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Wrapper.player().getInventory().getItem(i);
            if (stack.getItem() instanceof SwordItem) {
                double damage = damage(stack);
                if (damage > maxDamage) {
                    maxDamage = damage;
                    bestSlot = i;
                }
            }
        }
        if (bestSlot != -1) {
            equip(bestSlot);
        }
    }

    public void equipBestTool(final BlockState blockState) {
        int bestSlot = -1;
        double maxDamage = 0.0;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Wrapper.player().getInventory().getItem(i);
            double speed = stack.getDestroySpeed(blockState);
            if (speed > 1.0f) {
                speed += damageTool(stack, false);
                if (speed > maxDamage) {
                    maxDamage = speed;
                    bestSlot = i;
                }
            }
        }
        if (bestSlot != -1) {
            equip(bestSlot);
        }
    }

    public boolean onAutoTool() {
        for (final Hack hack : HackManager.getHack()) {
            if (!hack.autoTool && hack.isToggled()) {
                return false;
            }
        }
        return true;
    }
}
