package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HorseBaseEntity.class)
public class HorseBaseEntityMixin {
    @ModifyVariable(method = "computeFallDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float diminishFallDamage(float value) {
        return value * (float)Math.sqrt(GravityChangerAPI.getGravityStrength(((Entity) (Object) this)));
    }
}
