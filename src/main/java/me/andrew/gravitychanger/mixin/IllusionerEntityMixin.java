package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IllusionerEntity.class)
public abstract class IllusionerEntityMixin {
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

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getHeight() * 0.3333333333333333D, 0.0D, gravityDirection)).x;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getBodyY(D)D",
                    ordinal = 0
            )
    )
    private double redirect_attack_getBodyY_0(LivingEntity target, double heightScale) {
        if(!(target instanceof PlayerEntity)) {
            return target.getBodyY(heightScale);
        }
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) target;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getHeight() * 0.3333333333333333D, 0.0D, gravityDirection)).y;
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

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getHeight() * 0.3333333333333333D, 0.0D, gravityDirection)).z;
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
