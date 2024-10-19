package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.GravityPackets;
import com.fusionflux.gravity_api.util.networking.ClientboundAndServerboundPacketPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class UpdateGravityPacket extends GravityPacket implements ClientboundAndServerboundPacketPayload {
    public static StreamCodec<ByteBuf, UpdateGravityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.entityId,
            Gravity.STREAM_CODEC, p -> p.gravity,
            ByteBufCodecs.BOOL, p -> p.initialGravity,
            UpdateGravityPacket::new
    );
    
    public final Gravity gravity;
    public final boolean initialGravity;

    public UpdateGravityPacket(int entityId, Gravity gravity, boolean initialGravity) {
        this.entityId= entityId;
        this.gravity =  gravity;
        this.initialGravity = initialGravity;
    }

    @Override
    public void handleBoth(Player player, GravityComponent gc) {
        gc.addGravity(gravity, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return gravity.rotationParameters();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return GravityPackets.UPDATE_GRAVITY_PACKET;
    }
}
