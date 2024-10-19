package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;

public abstract class GravityPacket {
    public int entityId;
    
    public abstract RotationParameters getRotationParameters();
}
