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

public class Vec4 {
    public Double x = null;
    public Double y = null;
    public Double z = null;
    public Boolean onGround = null;

    public Vec4(final Object packet) {
        Object x = ReflectionHelper.getPrivateValue(packet, ClassManager.serverboundMovePlayerPacket_x);
        if (x instanceof Double x2) {
            this.x = x2;
        }
        Object y = ReflectionHelper.getPrivateValue(packet, ClassManager.serverboundMovePlayerPacket_y);
        if (y instanceof Double y2) {
            this.y = y2;
        }
        Object z = ReflectionHelper.getPrivateValue(packet, ClassManager.serverboundMovePlayerPacket_z);
        if (z instanceof Double z2) {
            this.z = z2;
        }
        Object onGround = ReflectionHelper.getPrivateValue(packet, ClassManager.serverboundMovePlayerPacket_onGround);
        if (onGround instanceof Boolean onGround2) {
            this.onGround = onGround2;
        }
    }

    public boolean success() {
        return this.x != null && this.y != null && this.z != null && this.onGround != null;
    }

}
