/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.player;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.TimerUtils;
import space.utils.Wrapper;
import space.value.IntValue;

public class AutoArmor extends Hack {
    public final TimerUtils speedPacket;
    private final IntValue maxDelay;
    private final IntValue minDelay;

    public AutoArmor() {
        super("AutoArmor", HackCategory.Player);
        this.maxDelay = new IntValue("MaxDelay", 200, 1, 400);
        this.minDelay = new IntValue("MinDelay", 150, 1, 390);
        this.addValue(this.maxDelay, this.minDelay);
        this.speedPacket = new TimerUtils();
    }

    public static float calculateArmorScore(ArmorItem armor, ItemStack stack) {
        float defenseScore = armor.getDefense() * 0.6f;
        float toughnessScore = armor.getToughness() * 0.2f;
        float durabilityRatio = 1 - (float) stack.getDamageValue() / stack.getMaxDamage();
        float durabilityScore = durabilityRatio * 0.2f;
        int protectionLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, stack);
        float enchantScore = protectionLevel * 0.1f;
        return defenseScore + toughnessScore + durabilityScore + enchantScore;
    }

    @Override
    public void onAllTick() {
        if (!(this.speedPacket.isDelayX(this.minDelay, this.maxDelay)) || Wrapper.controller() == null) {
            return;
        }
        this.speedPacket.setLastMS();
        ArmorList bestArmor = equipBestArmor();
        if (bestArmor.head.index != -1) {
            swapArmor(8 - bestArmor.head.index);
            swapArmor(bestArmor.head.index);
            return;
        }
        if (bestArmor.chest.index != -1) {
            swapArmor(8 - bestArmor.chest.index);
            swapArmor(bestArmor.chest.index);
            return;
        }
        if (bestArmor.legs.index != -1) {
            swapArmor(8 - bestArmor.legs.index);
            swapArmor(bestArmor.legs.index);
            return;
        }
        if (bestArmor.feet.index != -1) {
            swapArmor(8 - bestArmor.feet.index);
            swapArmor(bestArmor.feet.index);
        }
    }

    private ArmorList equipBestArmor() {
        ArmorList armorList = new ArmorList();
        for (int i = 0; i < Wrapper.player().getInventory().items.size(); i++) {
            ItemStack stack = Wrapper.player().getInventory().items.get(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armorItem)) {
                continue;
            }
            float currentScore = calculateArmorScore(armorItem, stack);
            int index = i;
            if (index < 9) {
                index += 36;
            }
            if (armorItem.getEquipmentSlot() == EquipmentSlot.HEAD) {
                if (currentScore > armorList.head.score) {
                    armorList.head.score = currentScore;
                    armorList.head.index = index;
                }
            } else if (armorItem.getEquipmentSlot() == EquipmentSlot.CHEST) {
                if (currentScore > armorList.chest.score) {
                    armorList.chest.score = currentScore;
                    armorList.chest.index = index;
                }
            } else if (armorItem.getEquipmentSlot() == EquipmentSlot.LEGS) {
                if (currentScore > armorList.legs.score) {
                    armorList.legs.score = currentScore;
                    armorList.legs.index = index;
                }
            } else if (armorItem.getEquipmentSlot() == EquipmentSlot.FEET) {
                if (currentScore > armorList.feet.score) {
                    armorList.feet.score = currentScore;
                    armorList.feet.index = index;
                }
            }
        }
        return armorList;
    }

    public void swapArmor(int index) {
        if (Wrapper.mc().gameMode != null) {
            Wrapper.mc().gameMode.handleInventoryMouseClick(0, index, 0, ClickType.QUICK_MOVE, Wrapper.player());
        }
    }

    static class ArmorList {
        ArmorPart head = new ArmorPart();
        ArmorPart chest = new ArmorPart();
        ArmorPart legs = new ArmorPart();
        ArmorPart feet = new ArmorPart();

        public ArmorList() {
            for (int i = 0; i < Wrapper.player().getInventory().armor.size(); ++i) {
                ItemStack stack = Wrapper.player().getInventory().armor.get(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armorItem)) {
                    continue;
                }
                if (armorItem.getEquipmentSlot() == EquipmentSlot.HEAD) {
                    this.head.score = calculateArmorScore(armorItem, stack);
                } else if (armorItem.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    this.chest.score = calculateArmorScore(armorItem, stack);
                } else if (armorItem.getEquipmentSlot() == EquipmentSlot.LEGS) {
                    this.legs.score = calculateArmorScore(armorItem, stack);
                } else if (armorItem.getEquipmentSlot() == EquipmentSlot.FEET) {
                    this.feet.score = calculateArmorScore(armorItem, stack);
                }
            }
        }
    }

    static class ArmorPart {
        int index = -1;
        float score = -1;
    }

}
