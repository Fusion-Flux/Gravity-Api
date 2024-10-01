package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class InvertGravityPacket extends GravityPacket {
    public static StreamCodec<ByteBuf, InvertGravityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, p -> p.inverted,
            RotationParameters.STREAM_CODEC, p -> p.rotationParameters,
            ByteBufCodecs.BOOL, p -> p.initialGravity,
            InvertGravityPacket::new
    );
    
    public final boolean inverted;
    public final RotationParameters rotationParameters;
    public final boolean initialGravity;

    public InvertGravityPacket(boolean _inverted, RotationParameters _rotationParameters, boolean _initialGravity){
        inverted = _inverted;
        rotationParameters = _rotationParameters;
        initialGravity = _initialGravity;
    }

    @Override
    public void run(GravityComponent gc) {
        gc.invertGravity(inverted, rotationParameters, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return rotationParameters;
    }
}
