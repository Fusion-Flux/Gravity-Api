package com.fusionflux.gravity_api.util;

import net.minecraft.util.math.Direction;

public class Gravity {
    Direction gravityDirection;
    int gravityDuration;
    int priority;
    String source;

    public Gravity(Direction gravDirection,int priority,int gravityDuration, String source) {
        this.gravityDirection = gravDirection;
        this.priority = priority;
        this.gravityDuration = gravityDuration;
        this.source = source;
    }

    public int getGravityDuration() {
        return gravityDuration;
    }

    public void decreaseDuration() {
        gravityDuration--;
    }

    public Direction getGravityDirection() {
        return gravityDirection;
    }
    public int getPriority() {
        return priority;
    }
    public String getSource() {
        return source;
    }

}
