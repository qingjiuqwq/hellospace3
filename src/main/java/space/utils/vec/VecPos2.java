/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils.vec;

public class VecPos2 extends VecPos {
    public boolean success;

    public VecPos2(final float yRot, final float xRot, final boolean success) {
        super(yRot, xRot);
        this.success = success;
    }
}
