package com.fusionflux.gravity_api.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class Gravity {
    private final Direction direction;
    private int duration;
    private final int priority;
    private final String source;

    public Gravity(Direction _direction, int _priority, int _duration, String _source) {
        direction = _direction;
        priority = _priority;
        duration = _duration;
        source = _source;
    }

    public Direction direction() {
        return direction;
    }
    public int duration() {
        return duration;
    }
    public int priority() {
        return priority;
    }
    public String source() {
        return source;
    }

    public void decrementDuration() {
        duration--;
    }
}
