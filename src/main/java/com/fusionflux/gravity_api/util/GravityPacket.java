package com.fusionflux.gravity_api.util;

import net.minecraft.network.PacketByteBuf;

public abstract class GravityPacket {
    public abstract void write(PacketByteBuf buf);
    public abstract void run(GravityComponent gc);
}
