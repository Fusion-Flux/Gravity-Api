package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin {
    @ModifyVariable(
            method = "attack",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private Vec3d modify_attack_Vec3d_0(Vec3d vec3d, LivingEntity target, float pullProgress) {
        if(target instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) target;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            vec3d = RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
        }

        return vec3d;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_attack_getX_0(LivingEntity target) {
        if(!(target instanceof PlayerEntity)) {
            return target.getX();
        }
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) target;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

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
        if(!(target instanceof PlayerEntity)) {
            return target.getEyeY();
        }
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) target;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

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
        if(!(target instanceof PlayerEntity)) {
            return target.getZ();
        }
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) target;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

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
        value = Math.sqrt(value);

        if(target instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) target;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            if(gravityDirection != Direction.DOWN) {
                value = Math.sqrt(value);
            }
        }

        return value;
    }
}
