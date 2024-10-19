package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.GravityPackets;
import com.fusionflux.gravity_api.util.networking.ClientboundAndServerboundPacketPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class DefaultGravityStrengthPacket extends GravityPacket implements ClientboundAndServerboundPacketPayload {
    public static StreamCodec<ByteBuf, DefaultGravityStrengthPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.entityId,
            ByteBufCodecs.DOUBLE, p -> p.strength,
            DefaultGravityStrengthPacket::new
    );
    
    public final double strength;

    public DefaultGravityStrengthPacket(int entityId, double strength) {
        this.entityId = entityId;
        this.strength = strength;
    }

    @Override
    public void handleBoth(Player player, GravityComponent gc) {
        gc.setDefaultGravityStrength(strength);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return null;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return GravityPackets.DEFAULT_GRAVITY_STRENGTH_PACKET;
    }
}