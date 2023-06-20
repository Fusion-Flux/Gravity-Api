package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {
    @ModifyConstant(method = "updateVelocity", constant = @Constant(doubleValue = -0.03999999910593033))
    private double multiplyGravity(double constant) {
        return constant * GravityChangerAPI.getGravityStrength(((Entity) (Object) this));
    }
}
