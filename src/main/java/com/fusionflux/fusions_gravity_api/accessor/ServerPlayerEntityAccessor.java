package com.fusionflux.fusions_gravity_api.accessor;

import net.minecraft.util.math.Direction;

public interface ServerPlayerEntityAccessor {
    void gravitychanger$sendGravityPacket(Direction gravityDirection, boolean initialGravity);
}
