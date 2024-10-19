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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OverwriteGravityPacket extends GravityPacket implements ClientboundAndServerboundPacketPayload {
    public static StreamCodec<ByteBuf, OverwriteGravityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.entityId,
            Gravity.STREAM_CODEC.apply(ByteBufCodecs.list()), p -> p.gravityList,
            ByteBufCodecs.BOOL, p -> p.initialGravity,
            OverwriteGravityPacket::new
    );
    
    public final List<Gravity> gravityList;
    public final boolean initialGravity;

    public OverwriteGravityPacket(int entityId, List<Gravity> gravityList, boolean initialGravity) {
        this.entityId = entityId;
        this.gravityList = gravityList;
        this.initialGravity = initialGravity;
    }

    @Override
    public void handleBoth(Player player, GravityComponent gc) {
        gc.setGravity(gravityList, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        Optional<Gravity> max = gravityList.stream().max(Comparator.comparingInt(Gravity::priority));
        if(max.isEmpty()) return new RotationParameters();
        return max.get().rotationParameters();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return GravityPackets.OVERWRITE_GRAVITY_PACKET;
    }
}
