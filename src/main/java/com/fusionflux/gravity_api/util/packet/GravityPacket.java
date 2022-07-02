package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.util.GravityComponent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class GravityPacket {
    public abstract void write(PacketByteBuf buf);
    public abstract void run(GravityComponent gc);
}
