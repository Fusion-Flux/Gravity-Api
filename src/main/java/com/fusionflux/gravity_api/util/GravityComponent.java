package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.api.RotationParameters;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.List;

public interface GravityComponent extends Component, CommonTickingComponent {
    //Internal

    @ApiStatus.Internal
    void onGravityChanged(Direction oldGravity, Direction newGravity, RotationParameters rotationParameters, boolean initialGravity);

    @ApiStatus.Internal
    void updateGravity(RotationParameters rotationParameters, boolean initialGravity);

    //Get

    Direction getGravityDirection();

    Direction getPrevGravityDirection();

    Direction getDefaultGravityDirection();

    double getGravityStrength();
    void setDefaultGravityStrength(double strength);

    double getDefaultGravityStrength();
    Direction getActualGravityDirection();

    List<Gravity> getGravity();

    boolean getInvertGravity();

    RotationAnimation getGravityAnimation();

    //Set

    void setGravity(List<Gravity> gravityList, boolean initialGravity);

    void invertGravity(boolean isInverted, RotationParameters rotationParameters, boolean initialGravity);

    void setDefaultGravityDirection(Direction gravityDirection, RotationParameters rotationParameters, boolean initialGravity);

    void addGravity(Gravity gravity, boolean initialGravity);

    void clearGravity(RotationParameters rotationParameters, boolean initialGravity);
}
