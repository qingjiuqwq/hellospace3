/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.another;

import space.hack.Hack;
import space.hack.HackCategory;
import space.value.Mode;
import space.value.ModeValue;

public class Teams extends Hack
{
    public Teams() {
        super("Teams", HackCategory.Another);
        this.addValue(
                new ModeValue("Mode", new Mode("Armor", true), new Mode("Base"))
        );
    }
}
