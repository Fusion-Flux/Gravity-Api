package me.andrew.gravitychanger.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.Direction;

public interface GravityComponent extends Component {

    void onGravityChanged(Direction prevGravityDirection, boolean initialGravity);

    Direction getTrackedGravityDirection();

    Direction getPrevTrackedGravityDirection();

    Direction getDefaultTrackedGravityDirection();

    void setTrackedGravityDirection(Direction gravityDirection, boolean initalGravity);

    void setDefaultTrackedGravityDirection(Direction gravityDirection);

    void setPrevTrackedGravityDirection(Direction gravityDirection);
}
