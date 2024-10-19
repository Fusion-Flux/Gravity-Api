package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.GravityPackets;
import com.fusionflux.gravity_api.util.networking.ClientboundAndServerboundPacketPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class DefaultGravityPacket extends GravityPacket implements ClientboundAndServerboundPacketPayload {
    public static StreamCodec<ByteBuf, DefaultGravityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.entityId,
            Direction.STREAM_CODEC, p -> p.direction,
            RotationParameters.STREAM_CODEC, DefaultGravityPacket::getRotationParameters,
            ByteBufCodecs.BOOL, p -> p.initialGravity,
            DefaultGravityPacket::new
    );
    
    public final Direction direction;
    public final RotationParameters rotationParameters;
    public final boolean initialGravity;

    public DefaultGravityPacket(int entityId, Direction direction, RotationParameters rotationParameters, boolean initialGravity) {
        this.entityId = entityId;
        this.direction = direction;
        this.rotationParameters = rotationParameters;
        this.initialGravity = initialGravity;
    }

    @Override
    public void handleBoth(Player player, GravityComponent gc) {
        gc.setDefaultGravityDirection(direction, rotationParameters, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return rotationParameters;
    }
    
    @Override
    public PacketTypeProvider getTypeProvider() {
        return GravityPackets.DEFAULT_GRAVITY_PACKET;
    }
}