package com.fusionflux.gravity_api.api;

import net.minecraft.util.math.Direction;

public class Gravity {
    private final Direction direction;
    private final int priority;
    private int duration;
    private final String source;
    private final RotationParameters rotationParameters;

    public Gravity(Direction _direction, int _priority, int _duration, String _source, RotationParameters _rotationParameters) {
        direction = _direction;
        priority = _priority;
        duration = _duration;
        source = _source;
        rotationParameters = _rotationParameters;
    }

    public Gravity(Direction _direction, int _priority, int _duration, String _source) {
        this(_direction, _priority, _duration, _source, new RotationParameters());
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
    public RotationParameters rotationParameters(){
        return rotationParameters;
    }

    public void decrementDuration() {
        duration--;
    }
}
