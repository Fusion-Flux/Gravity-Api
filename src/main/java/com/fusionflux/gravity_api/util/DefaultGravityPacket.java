package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.api.RotationParameters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

public class DefaultGravityPacket extends GravityPacket {
    private final Direction direction;
    private final RotationParameters rotationParameters;
    private final boolean initialGravity;

    DefaultGravityPacket(Direction _direction, RotationParameters _rotationParameters, boolean _initialGravity){
        direction = _direction;
        rotationParameters = _rotationParameters;
        initialGravity = _initialGravity;
    }

    @Override
    public GravityPacket read(PacketByteBuf buf) {
        return new DefaultGravityPacket(
                NetworkUtil.readDirection(buf),
                NetworkUtil.readRotationParameters(buf),
                buf.readBoolean()
        );
    }

    @Override
    public void write(PacketByteBuf buf) {
        NetworkUtil.writeDirection(buf, direction);
        NetworkUtil.writeRotationParameters(buf, rotationParameters);
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(GravityComponent gc) {
        gc.setDefaultGravityDirection(direction, rotationParameters, initialGravity);
    }
}
