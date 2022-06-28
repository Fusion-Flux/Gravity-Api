package com.fusionflux.gravity_api.api;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public class RotationParameters {
    private boolean rotateVelocity;
    private boolean rotateView;
    private boolean alternateCenter;
    private int rotationTime;//Milliseconds
    public RotationParameters(){
        this(
                !GravityChangerMod.config.worldVelocity,
                !GravityChangerMod.config.keepWorldLook,
                false,
                GravityChangerMod.config.rotationTime
        );
    }

    public RotationParameters(boolean _rotateVelocity, boolean _rotateView, boolean _alternateCenter, int _rotationTime){
        rotateVelocity = _rotateVelocity;
        rotateView = _rotateView;
        alternateCenter = _alternateCenter;
        rotationTime = _rotationTime;
    }

    public boolean rotateVelocity() {
        return rotateVelocity;
    }

    public boolean rotateView() {
        return rotateView;
    }

    public boolean alternateCenter() {
        return alternateCenter;
    }

    public int rotationTime() {
        return rotationTime;
    }

    @CanIgnoreReturnValue
    public RotationParameters rotateVelocity(boolean rotateVelocity) {
        this.rotateVelocity = rotateVelocity;
        return this;
    }

    @CanIgnoreReturnValue
    public RotationParameters rotateView(boolean rotateView) {
        this.rotateView = rotateView;
        return this;
    }

    @CanIgnoreReturnValue
    public RotationParameters alternateCenter(boolean alternateCenter) {
        this.alternateCenter = alternateCenter;
        return this;
    }

    @CanIgnoreReturnValue
    public RotationParameters rotationTime(int rotationTime) {
        this.rotationTime = rotationTime;
        return this;
    }
}
