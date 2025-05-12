/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.visual;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.ChatUtils;
import space.utils.Connection;
import space.utils.TimerUtils;
import space.utils.vec.VecPos;
import space.value.BooleanValue;

public class RotSpeed extends Hack {
    public final TimerUtils timer;
    public final TimerUtils posRotTimer;
    public final TimerUtils PosTimer;
    public final TimerUtils RotTimer;
    private final BooleanValue getPacket;
    private final BooleanValue postPacket;
    private final BooleanValue posRot;
    private final BooleanValue pos;
    private final BooleanValue rot;
    private final BooleanValue showSpeed;

    public RotSpeed() {
        super("RotSpeed", HackCategory.Visual);
        this.getPacket = new BooleanValue("GetPacket", false);
        this.postPacket = new BooleanValue("PostPacket", true);
        this.posRot = new BooleanValue("PosRot", true);
        this.pos = new BooleanValue("Pos", true);
        this.rot = new BooleanValue("Rot", true);
        this.showSpeed = new BooleanValue("ShowSpeed", true);
        this.addValue(this.getPacket, this.postPacket, this.posRot, this.pos, this.rot, this.showSpeed);
        this.timer = new TimerUtils();
        this.PosTimer = new TimerUtils();
        this.RotTimer = new TimerUtils();
        this.posRotTimer = new TimerUtils();
    }

    @Override
    public int onPacket(Object packet, Connection.Side side) {
        if (!this.isToggled()) {
            return super.onPacket(packet, side);
        }

        if (this.getPacket.getValue() && side != Connection.Side.IN) {
            return super.onPacket(packet, side);
        }

        if (this.postPacket.getValue() && side != Connection.Side.OUT) {
            return super.onPacket(packet, side);
        }

        Object object = null;
        String msg = null;

        // 类型 相同发包速度 转头信息 发包速度 发包模式
        if (this.posRot.getValue() && packet instanceof ServerboundMovePlayerPacket.PosRot) {
            object = packet;
            msg = "PosRot " + this.posRotTimer.getDelay();
            this.posRotTimer.setLastMS();
        } else if (this.pos.getValue() && packet instanceof ServerboundMovePlayerPacket.Pos) {
            object = packet;
            msg = "Pos " + this.PosTimer.getDelay();
            this.PosTimer.setLastMS();
        } else if (this.rot.getValue() && packet instanceof ServerboundMovePlayerPacket.Rot) {
            object = packet;
            msg = "Rot " + this.RotTimer.getDelay();
            this.RotTimer.setLastMS();
        }
        if (object == null) {
            return super.onPacket(packet, side);
        }
        VecPos vec = VecPos.getVecPos(object);
        StringBuilder str = new StringBuilder(msg).append(" ");
        if (vec == null) {
            str.append("null");
        } else {
            str.append(vec);
        }
        if (this.showSpeed.getValue()) {
            str.append(" ").append(this.timer.getDelay());
            this.timer.setLastMS();
        }
        str.append(" ");
        if (side == Connection.Side.IN) {
            str.append("IN");
        } else if (side == Connection.Side.OUT) {
            str.append("OUT");
        }
        ChatUtils.message(str.toString());

        return super.onPacket(packet, side);
    }
}
