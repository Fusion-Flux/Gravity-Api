package me.andrew.gravitychanger.mixin.client;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OtherClientPlayerEntity.class)
public abstract class OtherClientPlayerEntityMixin {
   // @Override
   // public Direction gravitychanger$getGravityDirection() {
   //     return this.gravitychanger$getTrackedGravityDirection();
   // }
//
   // @Override
   // public void gravitychanger$setGravityDirection(Direction gravityDirection, boolean initialGravity) {
   //     this.gravitychanger$setTrackedGravityDirection(gravityDirection);
   // }
}
