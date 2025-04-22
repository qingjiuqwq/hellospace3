/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils.vec;

public class VecPos {
    public float yRot;
    public float xRot;

    public VecPos(float yRot, float xRot) {
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public boolean equals(VecPos pos) {
        return pos.yRot == yRot && pos.xRot == xRot;
    }
}
