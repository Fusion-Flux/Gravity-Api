package me.andrew.gravitychanger.mixin.client;

import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OtherClientPlayerEntity.class)
public abstract class OtherClientPlayerEntityMixin implements RotatableEntityAccessor {
    @Override
    public Direction gravitychanger$getGravityDirection() {
        return this.gravitychanger$getTrackedGravityDirection();
    }

    @Override
    public void gravitychanger$setGravityDirection(Direction gravityDirection, boolean initialGravity) {
        this.gravitychanger$setTrackedGravityDirection(gravityDirection);
    }
}
