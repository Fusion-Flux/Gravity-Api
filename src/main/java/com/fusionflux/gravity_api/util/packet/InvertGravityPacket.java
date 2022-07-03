package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.NetworkUtil;
import net.minecraft.network.PacketByteBuf;

public class InvertGravityPacket extends GravityPacket{
    public final boolean inverted;
    public final RotationParameters rotationParameters;
    public final boolean initialGravity;

    public InvertGravityPacket(boolean _inverted, RotationParameters _rotationParameters, boolean _initialGravity){
        inverted = _inverted;
        rotationParameters = _rotationParameters;
        initialGravity = _initialGravity;
    }

    public InvertGravityPacket(PacketByteBuf buf) {
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

    @Override
    public RotationParameters getRotationParameters() {
        return rotationParameters;
    }
}
