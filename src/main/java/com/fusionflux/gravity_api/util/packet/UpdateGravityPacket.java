package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.util.GravityComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class UpdateGravityPacket extends GravityPacket {
    public static StreamCodec<ByteBuf, UpdateGravityPacket> STREAM_CODEC = StreamCodec.composite(
            Gravity.STREAM_CODEC, p -> p.gravity,
            ByteBufCodecs.BOOL, p -> p.initialGravity,
            UpdateGravityPacket::new
    );
    
    public final Gravity gravity;
    public final boolean initialGravity;

    public UpdateGravityPacket(Gravity _gravity, boolean _initialGravity) {
        gravity =  _gravity;
        initialGravity = _initialGravity;
    }

    @Override
    public void run(GravityComponent gc) {
        gc.addGravity(gravity, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return gravity.rotationParameters();
    }
}
