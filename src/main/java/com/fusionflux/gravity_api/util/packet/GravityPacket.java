package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.GravityComponent;

public abstract class GravityPacket {
    public abstract void run(GravityComponent gc);
    public abstract RotationParameters getRotationParameters();
}
