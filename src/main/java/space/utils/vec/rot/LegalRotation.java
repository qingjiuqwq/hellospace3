/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils.vec.rot;

import net.minecraft.world.entity.LivingEntity;
import space.utils.vec.VecPos;

public class LegalRotation {
    public VecPos pos;
    public Object packet;
    public LivingEntity target;

    public LegalRotation(final Object packet, final VecPos pos, final LivingEntity target) {
        this.pos = pos;
        this.packet = packet;
        this.target = target;
    }
}
