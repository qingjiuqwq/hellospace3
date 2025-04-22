/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.player;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.Connection;
import space.utils.ReflectionHelper;
import space.utils.Wrapper;
import space.value.Mode;
import space.value.ModeValue;

public class NoFall extends Hack {
    private final ModeValue mode;

    public NoFall() {
        super("NoFall", HackCategory.Player);
        this.mode = new ModeValue("Mode", new Mode("Simple", true), new Mode("AAC", false));
        this.addValue(this.mode);
    }

    @Override
    public void onAllTick() {
        if (this.mode.equals("AAC") && Wrapper.player().fallDistance > 2.0f) {
            Wrapper.sendPacket(new ServerboundMovePlayerPacket.StatusOnly(true));
        }
    }

    @Override
    public int onPacket(final Object packet, final Connection.Side side) {
        if (this.mode.equals("Simple") && side == Connection.Side.OUT && packet instanceof ServerboundMovePlayerPacket) {
            ReflectionHelper.setPrivateValue(packet, true, "onGround", "f_134123_");
        }
        return super.onPacket(packet, side);
    }
}
