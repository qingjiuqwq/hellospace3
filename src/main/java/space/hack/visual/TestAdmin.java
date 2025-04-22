/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.visual;

import space.hack.Hack;
import space.hack.HackCategory;
import space.value.IntValue;

public class TestAdmin extends Hack {
    public static int x = 0, y = 0, z = 0;
    private final IntValue x1;
    private final IntValue y1;
    private final IntValue z1;
    private final IntValue x2;
    private final IntValue y2;
    private final IntValue z2;

    public TestAdmin() {
        super("TestAdmin", HackCategory.Visual);
        this.x1 = new IntValue("X+", 0, 0, 100);
        this.y1 = new IntValue("Y+", 0, 0, 100);
        this.z1 = new IntValue("Z+", 0, 0, 100);

        this.x2 = new IntValue("X-", 0, 0, 100);
        this.y2 = new IntValue("Y-", 0, 0, 100);
        this.z2 = new IntValue("Z-", 0, 0, 100);
        this.addValue(this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    @Override
    public void onAllTick() {
        x = this.x1.getValue() - this.x2.getValue();
        y = this.y1.getValue() - this.y2.getValue();
        z = this.z1.getValue() - this.z2.getValue();
    }

}
