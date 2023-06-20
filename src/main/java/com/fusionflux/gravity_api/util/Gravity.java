package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.api.RotationParameters;
import net.minecraft.util.math.Direction;

public class Gravity {
    private final Direction direction;
    private final int priority;
    private int duration;
    private final double strength;
    private final String source;
    private final RotationParameters rotationParameters;
    public Gravity(Direction _direction, int _priority, double _strength, int _duration, String _source, RotationParameters _rotationParameters) {
        direction = _direction;
        priority = _priority;
        duration = _duration;
        source = _source;
        strength = _strength;
        rotationParameters = _rotationParameters;
    }

    public Gravity(Direction _direction, int _priority, int _duration, String _source, RotationParameters _rotationParameters) {
        this(_direction, _priority,1, _duration, _source, _rotationParameters);
    }
    public Gravity(Direction _direction, int _priority, int _duration, String _source) {
        this(_direction, _priority,1, _duration, _source, new RotationParameters());
    }

    public Gravity(Direction _direction, int _priority, double _strength, int _duration, String _source) {
        this(_direction, _priority, _strength, _duration, _source, new RotationParameters());
    }

    public Direction direction() {
        return direction;
    }
    public int duration() {
        return duration;
    }
    public double strength() {
        return strength;
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
