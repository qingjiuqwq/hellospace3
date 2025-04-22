/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import space.manager.ClassManager;
import space.manager.HackManager;

public class Connection extends ChannelDuplexHandler {

    public final TimerUtils rotSpeed = new TimerUtils();

    public Connection() {
        try {
            ClientPacketListener clientPacketListener = Wrapper.mc().getConnection();
            if (clientPacketListener != null) {
                ChannelPipeline pipeline = clientPacketListener.getConnection().channel().pipeline();
                pipeline.addBefore("packet_handler", "PacketHandler", this);
            }
        } catch (Exception exception) {
            ChatUtils.error("Connection: Error on attaching");
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object packet) throws Exception {
        HackManager.rPacket rPacket = HackManager.onPacket(packet, Side.IN);
        if (rPacket.suc) {
            super.channelRead(ctx, packet);
        }
        for (HackManager.onPacket on : rPacket.onPacket) {
            on.hack.onPFixes(packet, Side.IN, on.send);
        }
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object packet, final ChannelPromise promise) throws Exception {
        HackManager.rPacket rPacket = HackManager.onPacket(packet, Side.OUT);
        if (rPacket.suc) {
            if (packet instanceof ServerboundMovePlayerPacket.Rot || packet instanceof ServerboundMovePlayerPacket.Pos || packet instanceof ServerboundMovePlayerPacket.PosRot) {
                // System.out.println("Delay: " + packet.getClass().getName() + " " + this.rotSpeed.getDelay());
                if (packet instanceof ServerboundMovePlayerPacket.Rot || packet instanceof ServerboundMovePlayerPacket.PosRot) {
                    Object yRot1 = ReflectionHelper.getPrivateValue(packet, ClassManager.serverboundMovePlayerPacket_yRot);
                    Object xRot1 = ReflectionHelper.getPrivateValue(packet, ClassManager.serverboundMovePlayerPacket_xRot);
                    if (yRot1 != null && xRot1 != null) {
                        float yRot = (float) yRot1;
                        float xRot = (float) xRot1;
                        // ChatUtils.message(yRot + " " + xRot);
                    }
                }
                this.rotSpeed.setLastMS();
            }
            super.write(ctx, packet, promise);
        }
        for (HackManager.onPacket on : rPacket.onPacket) {
            on.hack.onPFixes(packet, Side.OUT, on.send);
        }
    }

    public enum Side {
        IN,
        OUT
    }
}
