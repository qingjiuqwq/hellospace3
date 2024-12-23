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
import space.value.BooleanValue;

public class Targets extends Hack
{
    public Targets() {
        super("Targets", HackCategory.Another);
        this.addValue(
                new BooleanValue("Players",true),
                new BooleanValue("Mobs", true),
                new BooleanValue("Invisible", false),
                new BooleanValue("Sleeping", false)
        );
        this.setShow(false);
    }
}
