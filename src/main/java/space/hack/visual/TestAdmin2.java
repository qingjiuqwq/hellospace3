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
import space.value.BooleanValue;

public class TestAdmin2 extends Hack {
    public final BooleanValue boo1X;
    public final BooleanValue boo2X;
    public final BooleanValue boo3X;
    public final BooleanValue boo4X;
    public final BooleanValue boo5X;
    public final BooleanValue boo6X;
    public static boolean boo1 = false, boo2 = false, boo3 = false, boo4 = false, boo5 = false, boo6 = false;

    @Override
    public void onAllTick() {
        boo1 = boo1X.getValue();
        boo2 = boo2X.getValue();
        boo3 = boo3X.getValue();
        boo4 = boo4X.getValue();
        boo5 = boo5X.getValue();
        boo6 = boo6X.getValue();
    }

    public TestAdmin2() {
        super("TestAdmin2", HackCategory.Visual);
        this.boo1X = new BooleanValue("boo1", false);
        this.boo2X = new BooleanValue("boo2", false);
        this.boo3X = new BooleanValue("boo3", false);
        this.boo4X = new BooleanValue("boo4", false);
        this.boo5X = new BooleanValue("boo5", false);
        this.boo6X = new BooleanValue("boo6", false);
        this.addValue(this.boo1X, this.boo2X, this.boo3X, this.boo4X, this.boo5X, this.boo6X);
    }
     
}
