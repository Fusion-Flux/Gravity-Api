package me.andrew.gravitychanger.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

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
