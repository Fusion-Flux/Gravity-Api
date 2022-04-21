package me.andrew.gravitychanger.accessor;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.Direction;

public interface RotatableEntityAccessor {
    default Direction gravitychanger$getGravityDirection() {
        return this.gravitychanger$getTrackedGravityDirection();
    }

    default Direction gravitychanger$getDefaultGravityDirection() {
        return this.gravitychanger$getDefaultTrackedGravityDirection();
    }

    default void gravitychanger$setGravityDirection(Direction gravityDirection, boolean initialGravity) {
        this.gravitychanger$setTrackedGravityDirection(gravityDirection);
    }

    default void gravitychanger$setDefaultGravityDirection(Direction gravityDirection, boolean initialGravity) {
        this.gravitychanger$setDefaultTrackedGravityDirection(gravityDirection);
    }

    void gravitychanger$onGravityChanged(Direction prevGravityDirection, boolean initialGravity);

    Direction gravitychanger$getTrackedGravityDirection();

    Direction gravitychanger$getDefaultTrackedGravityDirection();

    void gravitychanger$setTrackedGravityDirection(Direction gravityDirection);

    void gravitychanger$setDefaultTrackedGravityDirection(Direction gravityDirection);

    void gravitychanger$onTrackedData(TrackedData<?> data);
}
