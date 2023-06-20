package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity {
    public TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @ModifyArg(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"
        ),
        index = 1
    )
    private double multiplyGravity(double x) {
        return x * GravityChangerAPI.getGravityStrength(this);
    }
}
