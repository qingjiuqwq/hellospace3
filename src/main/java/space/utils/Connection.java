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
import net.minecraft.network.protocol.game.*;
import space.manager.HackManager;

public class Connection extends ChannelDuplexHandler
{

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
        if (packet instanceof ClientboundSetEntityDataPacket ||
                packet instanceof ChannelHandlerContext ||
                packet instanceof ClientboundEntityEventPacket ||
                packet instanceof ClientboundRotateHeadPacket ||
                packet instanceof ClientboundMoveEntityPacket.Pos ||
                packet instanceof ClientboundMoveEntityPacket.PosRot ||
                packet instanceof ClientboundTeleportEntityPacket ||
                packet instanceof ClientboundSetTimePacket ||
                packet instanceof ClientboundKeepAlivePacket ||
                packet instanceof ClientboundPlayerInfoPacket ||
                packet instanceof ClientboundSetEntityMotionPacket){
        }else {
            System.out.println(packet.getClass().getName());
        }
        if (HackManager.onPacket(packet, Side.IN)) {
            return;
        }
        super.channelRead(ctx, packet);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object packet, final ChannelPromise promise) throws Exception {
        if (packet instanceof ServerboundMovePlayerPacket.PosRot ||
                packet instanceof ServerboundSwingPacket ||
                packet instanceof ServerboundKeepAlivePacket ||
                packet instanceof ServerboundMovePlayerPacket.Pos ||
                packet instanceof ServerboundContainerClickPacket ||
                packet instanceof ServerboundAcceptTeleportationPacket ||
                packet instanceof ServerboundMovePlayerPacket.Rot){
        }else {
            System.out.println(packet.getClass().getName());
        }
        if (HackManager.onPacket(packet, Side.OUT)) {
            return;
        }
        super.write(ctx, packet, promise);
    }

    public enum Side
    {
        IN,
        OUT
    }
}
