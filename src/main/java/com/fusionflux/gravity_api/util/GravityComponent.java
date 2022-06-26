package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.RotationAnimation;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public interface GravityComponent extends Component {

    void onGravityChanged(Direction from, Direction to, boolean initialGravity);

    Direction getGravityDirection();

    void updateGravity(boolean initalGravity);

    Direction getPrevGravityDirection();

    Direction getDefaultGravityDirection();

    void setDefaultGravityDirection(Direction gravityDirection, int animationDurationMs);

    void addGravity(Gravity gravity, boolean initialGravity);

    ArrayList<Gravity> getGravity();

    void setGravity(ArrayList<Gravity> gravityList,boolean initalGravity);

    void invertGravity(boolean isInverted);

    boolean getInvertGravity();

    void clearGravity();

    RotationAnimation getGravityAnimation();
}
