package com.fusionflux.gravity_api.api;

import com.fusionflux.gravity_api.config.GravityChangerConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class RotationParameters {
    public static final StreamCodec<ByteBuf, RotationParameters> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RotationParameters::rotateVelocity,
            ByteBufCodecs.BOOL, RotationParameters::rotateView,
            ByteBufCodecs.BOOL, RotationParameters::alternateCenter,
            ByteBufCodecs.INT, RotationParameters::rotationTime,
            RotationParameters::new
    );
    
    private boolean rotateVelocity;
    private boolean rotateView;
    private boolean alternateCenter;
    private int rotationTime;//Milliseconds
    
    public RotationParameters(){
        this(
                GravityChangerConfig.worldVelocity,
                !GravityChangerConfig.keepWorldLook,
                false,
                GravityChangerConfig.rotationTime
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

    public RotationParameters rotateVelocity(boolean rotateVelocity) {
        this.rotateVelocity = rotateVelocity;
        return this;
    }

    public RotationParameters rotateView(boolean rotateView) {
        this.rotateView = rotateView;
        return this;
    }

    public RotationParameters alternateCenter(boolean alternateCenter) {
        this.alternateCenter = alternateCenter;
        return this;
    }

    public RotationParameters rotationTime(int rotationTime) {
        this.rotationTime = rotationTime;
        return this;
    }
}
