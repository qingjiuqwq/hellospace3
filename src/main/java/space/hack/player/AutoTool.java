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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import space.hack.Hack;
import space.hack.HackCategory;
import space.manager.HackManager;
import space.utils.Wrapper;
import space.value.Mode;
import space.value.ModeValue;

public class AutoTool extends Hack
{
    public final ModeValue priority;

    public AutoTool() {
        super("AutoTool", HackCategory.Player);
        this.priority = new ModeValue("Priority", new Mode("All", true), new Mode("Weapon"),new Mode("Tool"));
        this.addValue(this.priority);
    }

    @Override
    public void onLeftClickBlock(final BlockPos event) {
        if (onAutoTool() && !this.priority.getValue("Weapon").isToggled()){
            this.equipBestTool(Wrapper.level().getBlockState(event));
        }
    }

    @Override
    public void onAttack(final Entity event) {
        if (onAutoTool() && !this.priority.getValue("Tool").isToggled()){
            equipBestWeapon();
        }
    }

    public static void equipBestWeapon() {
        int bestSlot = -1;
        double maxDamage = 0.0;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Wrapper.player().getInventory().getItem(i);
            if (stack.getItem() instanceof SwordItem) {
                final double damage = damage(stack);
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

    public boolean onAutoTool() {
        for (final Hack hack : HackManager.getHack()) {
            if (!hack.AutoTool && hack.isToggled()) {
                return false;
            }
        }
        return true;
    }


    public static double damage(final ItemStack stack){
        return ((SwordItem)stack.getItem()).getDamage() + EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED) + 1.0 + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, stack) * 0.6000000238418579;
    }

    public void equipBestTool(final BlockState blockState){
        int bestSlot = -1;
        double max = 0.0;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Wrapper.player().getInventory().getItem(i);
            float speed = stack.getDestroySpeed(blockState);
            if (speed > 1.0f) {
                final int eff;
                speed += (float)(((eff = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack)) > 0) ? (Math.pow(eff, 2.0) + 1.0) : 0.0);
                if (speed > max) {
                    max = speed;
                    bestSlot = i;
                }
            }
        }
        if (bestSlot != -1) {
            equip(bestSlot);
        }
    }

    public static void equip(final int slot) {
        Wrapper.player().getInventory().selected = slot;
    }
}
