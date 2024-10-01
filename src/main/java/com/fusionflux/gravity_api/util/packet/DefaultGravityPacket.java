package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class DefaultGravityPacket extends GravityPacket {
    public static StreamCodec<ByteBuf, DefaultGravityPacket> STREAM_CODEC = StreamCodec.composite(
            Direction.STREAM_CODEC, p -> p.direction,
            RotationParameters.STREAM_CODEC, DefaultGravityPacket::getRotationParameters,
            ByteBufCodecs.BOOL, p -> p.initialGravity,
            DefaultGravityPacket::new
    );
    
    public final Direction direction;
    public final RotationParameters rotationParameters;
    public final boolean initialGravity;

    public DefaultGravityPacket(Direction _direction, RotationParameters _rotationParameters, boolean _initialGravity){
        direction = _direction;
        rotationParameters = _rotationParameters;
        initialGravity = _initialGravity;
    }

    @Override
    public void run(GravityComponent gc) {
        gc.setDefaultGravityDirection(direction, rotationParameters, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return rotationParameters;
    }
}