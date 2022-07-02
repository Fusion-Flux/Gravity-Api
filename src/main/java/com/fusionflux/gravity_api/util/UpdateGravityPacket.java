package com.fusionflux.gravity_api.util;

import net.minecraft.network.PacketByteBuf;

public class UpdateGravityPacket extends GravityPacket{
    private final Gravity gravity;
    private final boolean initialGravity;

    UpdateGravityPacket(Gravity _gravity, boolean _initialGravity){
        gravity =  _gravity;
        initialGravity = _initialGravity;
    }

    UpdateGravityPacket(PacketByteBuf buf) {
        this(
            NetworkUtil.readGravity(buf),
            buf.readBoolean()
        );
    }

    @Override
    public void write(PacketByteBuf buf) {
        NetworkUtil.writeGravity(buf, gravity);
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(GravityComponent gc) {
        gc.addGravity(gravity, initialGravity);
    }
}
