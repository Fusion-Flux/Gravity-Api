package me.andrew.gravitychanger.accessor;

import net.minecraft.util.math.Direction;

public interface ServerPlayerEntityAccessor {
    void gravitychanger$sendGravityPacket(Direction gravityDirection, boolean initialGravity);
}
