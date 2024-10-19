package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.GravityPackets;
import com.fusionflux.gravity_api.util.networking.ClientboundAndServerboundPacketPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class InvertGravityPacket extends GravityPacket implements ClientboundAndServerboundPacketPayload {
    public static StreamCodec<ByteBuf, InvertGravityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.entityId,
            ByteBufCodecs.BOOL, p -> p.inverted,
            RotationParameters.STREAM_CODEC, p -> p.rotationParameters,
            ByteBufCodecs.BOOL, p -> p.initialGravity,
            InvertGravityPacket::new
    );
    
    public final boolean inverted;
    public final RotationParameters rotationParameters;
    public final boolean initialGravity;

    public InvertGravityPacket(int entityId, boolean inverted, RotationParameters rotationParameters, boolean initialGravity){
        this.entityId = entityId;
        this.inverted = inverted;
        this.rotationParameters = rotationParameters;
        this.initialGravity = initialGravity;
    }

    @Override
    public void handleBoth(Player player, GravityComponent gc) {
        gc.invertGravity(inverted, rotationParameters, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return rotationParameters;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return GravityPackets.INVERT_GRAVITY_PACKET;
    }
}
