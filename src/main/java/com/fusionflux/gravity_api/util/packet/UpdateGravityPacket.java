package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.NetworkUtil;
import net.minecraft.network.PacketByteBuf;

public class UpdateGravityPacket extends GravityPacket{
    public final Gravity gravity;
    public final boolean initialGravity;

    public UpdateGravityPacket(Gravity _gravity, boolean _initialGravity){
        gravity =  _gravity;
        initialGravity = _initialGravity;
    }

    public UpdateGravityPacket(PacketByteBuf buf) {
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

    @Override
    public RotationParameters getRotationParameters() {
        return gravity.rotationParameters();
    }
}
