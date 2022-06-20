package com.fusionflux.gravity_api.mixin.client;

import net.minecraft.client.network.OtherClientPlayerEntity;
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
