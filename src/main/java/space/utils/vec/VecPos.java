/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils.vec;

import space.manager.ClassManager;
import space.utils.ReflectionHelper;

public class VecPos {
    public float yRot;
    public float xRot;

    public VecPos() {
        this.yRot = 0;
        this.xRot = 0;
    }

    public VecPos(final float yRot, final float xRot) {
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public static VecPos getVecPos(final Object packet) {
        VecPos vecPos = new VecPos();
        Object yRot1 = ReflectionHelper.getPrivateValue(packet, ClassManager.serverboundMovePlayerPacket_yRot);
        Object xRot1 = ReflectionHelper.getPrivateValue(packet, ClassManager.serverboundMovePlayerPacket_xRot);
        if (!(yRot1 instanceof Float yRot2) || !(xRot1 instanceof Float xRot2)) {
            return null;
        }
        vecPos.xRot = xRot2;
        vecPos.yRot = yRot2;
        return vecPos;
    }

    public boolean equals(VecPos pos) {
        return pos.yRot == yRot && pos.xRot == xRot;
    }

    @Override
    public String toString() {
        return yRot + " " + xRot;
    }
}
