package com.fusionflux.gravity_api.util;

import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;

public class OverwriteGravityPacket extends GravityPacket {
    private final ArrayList<Gravity> gravityList;
    private final boolean initialGravity;

    OverwriteGravityPacket(ArrayList<Gravity> _gravityList, boolean _initialGravity){
        gravityList = _gravityList;
        initialGravity = _initialGravity;
    }

    OverwriteGravityPacket(PacketByteBuf buf) {
        int listSize = buf.readInt();
        gravityList = new ArrayList<>();
        for (int i = 0; i < listSize; i++)
            gravityList.add(NetworkUtil.readGravity(buf));
        initialGravity = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(gravityList.size());
        for (Gravity gravity : gravityList) NetworkUtil.writeGravity(buf, gravity);
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(GravityComponent gc) {
        gc.setGravity(gravityList, initialGravity);
    }
}
