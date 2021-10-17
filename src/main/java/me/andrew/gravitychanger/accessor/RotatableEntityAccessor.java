package me.andrew.gravitychanger.accessor;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.Direction;

public interface RotatableEntityAccessor {
    Direction gravitychanger$getGravityDirection();

    void gravitychanger$setGravityDirection(Direction direction);

    void gravitychanger$onTrackedData(TrackedData<?> data);
}
