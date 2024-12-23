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
import space.value.NumberValue;

public class TestAdmin1 extends Hack {
    public final NumberValue num1X;
    public final NumberValue num2X;
    public final NumberValue num3X;
    public final NumberValue num4X;
    public final NumberValue num5X;
    public final NumberValue num6X;
    public static int num1 = 0, num2 = 0, num3 = 0, num4 = 0, num5 = 0, num6 = 0;

    @Override
    public void onAllTick() {
        num1 = num1X.getValue().intValue();
        num2 = num2X.getValue().intValue();
        num3 = num3X.getValue().intValue();
        num4 = num4X.getValue().intValue();
        num5 = num5X.getValue().intValue();
        num6 = num6X.getValue().intValue();
    }

    public TestAdmin1() {
        super("TestAdmin1", HackCategory.Visual);
        this.num1X = new NumberValue("num1", 0.0, 0.0, 300.0);
        this.num2X = new NumberValue("num2", 0.0, 0.0, 300.0);
        this.num3X = new NumberValue("num3", 0.0, 0.0, 300.0);
        this.num4X = new NumberValue("num4", 0.0, 0.0, 300.0);
        this.num5X = new NumberValue("num5", 0.0, 0.0, 300.0);
        this.num6X = new NumberValue("num6", 0.0, 0.0, 300.0);
        this.addValue(this.num1X, this.num2X, this.num3X, this.num4X, this.num5X, this.num6X);
    }
     
}
