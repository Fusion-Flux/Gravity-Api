package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.api.RotationParameters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

public class InvertGravityPacket extends GravityPacket{
    private final boolean inverted;
    private final RotationParameters rotationParameters;
    private final boolean initialGravity;

    InvertGravityPacket(boolean _inverted, RotationParameters _rotationParameters, boolean _initialGravity){
        inverted = _inverted;
        rotationParameters = _rotationParameters;
        initialGravity = _initialGravity;
    }

    InvertGravityPacket(PacketByteBuf buf) {
        this(
            buf.readBoolean(),
            NetworkUtil.readRotationParameters(buf),
            buf.readBoolean()
        );
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBoolean(inverted);
        NetworkUtil.writeRotationParameters(buf, rotationParameters);
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(GravityComponent gc) {
        gc.invertGravity(inverted, rotationParameters, initialGravity);
    }
}
