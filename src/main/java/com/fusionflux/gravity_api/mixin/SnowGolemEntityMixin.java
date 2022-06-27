package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnowGolemEntity.class)
public abstract class SnowGolemEntityMixin {
    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_attack_getX_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getStandingEyeHeight() - 1.100000023841858D, 0.0D, gravityDirection)).x;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_attack_getEyeY_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN) {
            return target.getEyeY();
        }

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getStandingEyeHeight() - 1.100000023841858D, 0.0D, gravityDirection)).y + 1.100000023841858D;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_attack_getZ_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN) {
            return target.getZ();
        }

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getStandingEyeHeight() - 1.100000023841858D, 0.0D, gravityDirection)).z;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;sqrt(D)D"
            )
    )
    private double redirect_attack_sqrt_0(double value, LivingEntity target, float pullProgress) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN) {
            return Math.sqrt(value);
        }

        return Math.sqrt(Math.sqrt(value));
    }
}
