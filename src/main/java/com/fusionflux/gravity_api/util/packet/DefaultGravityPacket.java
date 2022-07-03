package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.NetworkUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

public class DefaultGravityPacket extends GravityPacket {
    public final Direction direction;
    public final RotationParameters rotationParameters;
    public final boolean initialGravity;

    public DefaultGravityPacket(Direction _direction, RotationParameters _rotationParameters, boolean _initialGravity){
        direction = _direction;
        rotationParameters = _rotationParameters;
        initialGravity = _initialGravity;
    }

    public DefaultGravityPacket(PacketByteBuf buf){
        this(NetworkUtil.readDirection(buf), NetworkUtil.readRotationParameters(buf), buf.readBoolean());
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

    @Override
    public RotationParameters getRotationParameters() {
        return rotationParameters;
    }
}