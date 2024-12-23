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
import space.value.BooleanValue;

public class AntiBot extends Hack
{

    public AntiBot() {
        super("AntiBot", HackCategory.Another);
        this.addValue(
                new BooleanValue("Armor", true)
        );
    }

    public static boolean isBot(final Player bot, final Hack hack) {
        if (hack.isBooleanValue("Armor")) {
            if (Armor(bot)) {
                return true;
            }
        }
        return false;

    }

    public static boolean Armor(final Player bot){
        return bot.getInventory().armor.get(0).isEmpty() && bot.getInventory().armor.get(1).isEmpty()
                && bot.getInventory().armor.get(2).isEmpty() && bot.getInventory().armor.get(3).isEmpty();
    }
}