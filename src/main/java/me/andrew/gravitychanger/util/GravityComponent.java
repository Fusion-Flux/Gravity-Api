package me.andrew.gravitychanger.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public interface GravityComponent extends Component {

    void onGravityChanged(Direction prevGravityDirection, boolean initialGravity);

    Direction getTrackedGravityDirection();

    void updateGravity(boolean initalGravity);

    Direction getPrevTrackedGravityDirection();

    void setPrevTrackedGravityDirection(Direction gravityDirection);

    Direction getDefaultTrackedGravityDirection();

    void setDefaultTrackedGravityDirection(Direction gravityDirection);

    void addGravity(Gravity gravity, boolean initialGravity);

    ArrayList<Gravity> getGravity();

    void setGravity(ArrayList<Gravity> gravityList,boolean initalGravity,boolean needsUpdate);

    void invertGravity(boolean isInverted);

    boolean getInvertGravity();

    void clearGravity();
}
