package me.andrew.gravitychanger.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.Direction;

interface GravityComponent extends Component {

    default Direction getGravityDirection() {
        return this.getTrackedGravityDirection();
    }

    default Direction getDefaultGravityDirection() {
        return this.getDefaultTrackedGravityDirection();
    }

    default void setGravityDirection(Direction gravityDirection, boolean initialGravity) {
        this.setTrackedGravityDirection(gravityDirection);
    }

    default void setDefaultGravityDirection(Direction gravityDirection, boolean initialGravity) {
        this.setDefaultTrackedGravityDirection(gravityDirection);
    }

    void onGravityChanged(Direction prevGravityDirection, boolean initialGravity);

    Direction getTrackedGravityDirection();

    Direction getDefaultTrackedGravityDirection();

    void setTrackedGravityDirection(Direction gravityDirection);

    void setDefaultTrackedGravityDirection(Direction gravityDirection);

    void onTrackedData(TrackedData<?> data);
}
