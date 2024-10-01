package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class DefaultGravityStrengthPacket extends GravityPacket {
    public static StreamCodec<ByteBuf, DefaultGravityStrengthPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, p -> p.strength,
            DefaultGravityStrengthPacket::new
    );
    
    public final double strength;

    public DefaultGravityStrengthPacket(double _strength){
        strength = _strength;
    }
    
    @Override
    public void run(GravityComponent gc) {
        gc.setDefaultGravityStrength(strength);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return null;
    }
}