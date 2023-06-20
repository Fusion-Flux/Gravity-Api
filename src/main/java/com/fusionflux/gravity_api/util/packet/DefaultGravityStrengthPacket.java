package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;
import net.minecraft.network.PacketByteBuf;

public class DefaultGravityStrengthPacket extends GravityPacket {
    public final double strength;

    public DefaultGravityStrengthPacket(double _strength){
        strength = _strength;
    }

    public DefaultGravityStrengthPacket(PacketByteBuf buf){
        this(buf.readDouble());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeDouble(strength);
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