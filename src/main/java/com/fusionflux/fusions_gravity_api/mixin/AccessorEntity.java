package com.fusionflux.fusions_gravity_api.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Invoker("calculateBoundingBox")
    Box gravity$calculateBoundingBox();
}
