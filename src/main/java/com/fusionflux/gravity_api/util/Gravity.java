package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public class Gravity {
    public static final StreamCodec<ByteBuf, Gravity> STREAM_CODEC = StreamCodec.composite(
            Direction.STREAM_CODEC, Gravity::direction,
            ByteBufCodecs.INT, Gravity::priority,
            ByteBufCodecs.optional(ByteBufCodecs.DOUBLE), g -> Optional.of(g.strength()),
            ByteBufCodecs.INT, Gravity::duration,
            ByteBufCodecs.STRING_UTF8, Gravity::source,
            ByteBufCodecs.optional(RotationParameters.STREAM_CODEC), g -> Optional.of(g.rotationParameters()),
            (direction, priority, strengthOptional, duration, source, rotationParametersOptional) -> 
                new Gravity(
                        direction,
                        priority,
                        strengthOptional.orElse(1D),
                        duration,
                        source,
                        rotationParametersOptional.orElse(new RotationParameters())
                )
    );
    
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gravity gravity = (Gravity) o;
        return priority == gravity.priority && duration == gravity.duration && Double.compare(strength, gravity.strength) == 0 && direction == gravity.direction && source.equals(gravity.source) && rotationParameters.equals(gravity.rotationParameters);
    }

    @Override
    public int hashCode() {
        int result = direction.hashCode();
        result = 31 * result + priority;
        result = 31 * result + duration;
        result = 31 * result + Double.hashCode(strength);
        result = 31 * result + source.hashCode();
        result = 31 * result + rotationParameters.hashCode();
        return result;
    }
}
