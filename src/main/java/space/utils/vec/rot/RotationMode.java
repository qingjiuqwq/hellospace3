/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils.vec.rot;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import space.utils.TimerUtils;
import space.utils.Utils;
import space.utils.Wrapper;
import space.utils.vec.Vec4;
import space.utils.vec.VecPos;

import java.util.ArrayList;

public class RotationMode {

    public final TimerUtils speedPacket;
    private final ArrayList<LegalRotation> legalPacket;
    public VecPos pos;
    public Vec3 xyz;
    public boolean success;
    public VecPos current;
    public LivingEntity target;
    public VecPos oldTargetPos;

    public RotationMode() {
        this.oldTargetPos = null;
        this.legalPacket = new ArrayList<>();
        this.speedPacket = new TimerUtils();
    }

    public void clear(final boolean current) {
        this.setLastMS();
        this.pos = null;
        this.target = null;
        this.success = false;
        this.legalPacket.clear();
        if (current) {
            this.xyz = new Vec3(Wrapper.player().getX(), Wrapper.player().getY(), Wrapper.player().getZ());
            this.current = new VecPos(Wrapper.player().getYRot(), Wrapper.player().getXRot());
        }
    }

    public boolean sendPacket() {
        boolean success = this.speedPacket.isDelayX(50, 60);
        if (success) {
            this.speedPacket.setLastMS();
        }
        return success;
    }

    public void setLastMS() {
        this.speedPacket.setLastMS();
    }

    public void xyz(final Vec4 vec4) {
        this.xyz = new Vec3(vec4.x, vec4.y, vec4.z);
        this.setLastMS();
    }

    public boolean xyzEq(final Vec4 vec4) {
        return this.xyz != null && this.xyz.x == vec4.x && this.xyz.y == vec4.y && this.xyz.z == vec4.z;
    }

    public boolean targetEq(final LivingEntity target) {
        return this.oldTargetPos == null || !this.oldTargetPos.equals(Utils.getSimpleRotations(target, 1));
    }

    public boolean targetEq(final LivingEntity target, Vec4 vec4) {
        return this.oldTargetPos != null && this.oldTargetPos.equals(Utils.getSimpleRotations(target, vec4, 1));
    }

    public void target(final LivingEntity target) {
        this.target(target, true);
    }

    public void target(final LivingEntity target, final boolean pos) {
        this.target = target;
        if (pos) {
            this.pos = Utils.getSimpleRotations(target);
            this.oldTargetPos = Utils.getSimpleRotations(target, 1);
        }
    }

    public void target(final LivingEntity target, final Vec4 player) {
        this.target(target, false);
        this.pos = Utils.getSimpleRotations(this.target, player);
        this.oldTargetPos = Utils.getSimpleRotations(this.target, player, 1);
    }

    public void add(final LegalRotation legalRotation) {
        this.legalPacket.add(legalRotation);
    }

    public int size() {
        return this.legalPacket.size();
    }

    public LegalRotation get(final int i) {
        return this.legalPacket.get(i);
    }

    public void remove(final int i) {
        this.legalPacket.remove(i);
    }

}
