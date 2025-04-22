/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.another;

import net.minecraft.world.entity.player.Player;
import space.hack.Hack;
import space.hack.HackCategory;
import space.hack.hud.element.EnemyInfo;
import space.utils.Wrapper;
import space.value.BooleanValue;

public class AntiBot extends Hack {

    public AntiBot() {
        super("AntiBot", HackCategory.Another);
        this.addValue(
                new BooleanValue("Armor", true),
                new BooleanValue("Invisible", true),
                new BooleanValue("PlayerInfo", true),
                new BooleanValue("StrangeItem", false),
                new BooleanValue("StrangeArmor", false)
        );
    }

    public static boolean isBot(final Player bot, final Hack hack) {
        if (hack.isBooleanValue("Armor") && armor(bot)) {
            return true;
        }
        if (hack.isBooleanValue("PlayerInfo") && playerInfo(bot)) {
            return true;
        }
        if (hack.isBooleanValue("StrangeItem") && strangeItem(bot)) {
            return true;
        }
        if (hack.isBooleanValue("Invisible") && bot.isInvisible()) {
            return true;
        }
        return hack.isBooleanValue("StrangeArmor") && strangeArmor(bot);
    }

    public static boolean strangeArmor(final Player bot) {
        for (int i = 0; i < bot.getInventory().armor.size(); i++) {
            if (Wrapper.isItemStack(bot.getInventory().armor.get(i).getItem())) {
                return true;
            }
        }
        return armor(bot) != armor(Wrapper.player());
    }

    public static boolean strangeItem(final Player bot) {
        if (Wrapper.isItemStack(bot.getMainHandItem().getItem())) {
            return true;
        }
        return Wrapper.isItemStack(bot.getOffhandItem().getItem());
    }

    public static boolean armor(final Player bot) {
        return bot.getInventory().armor.get(0).isEmpty() && bot.getInventory().armor.get(1).isEmpty()
                && bot.getInventory().armor.get(2).isEmpty() && bot.getInventory().armor.get(3).isEmpty();
    }

    public static boolean playerInfo(final Player bot) {
        return EnemyInfo.drawHead(bot) == null;
    }
}