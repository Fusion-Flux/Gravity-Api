package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin {
    @Redirect(
            method = "shootSkullAt(ILnet/minecraft/entity/LivingEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_shootSkullAt_getX_0(LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getStandingEyeHeight() * 0.5D, 0.0D, gravityDirection)).x;
    }

    @Redirect(
            method = "shootSkullAt(ILnet/minecraft/entity/LivingEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getY()D",
                    ordinal = 0
            )
    )
    private double redirect_shootSkullAt_getY_0(LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getStandingEyeHeight() * 0.5D, 0.0D, gravityDirection)).y - target.getStandingEyeHeight() * 0.5D;
    }

    @Redirect(
            method = "shootSkullAt(ILnet/minecraft/entity/LivingEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_shootSkullAt_getZ_0(LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getStandingEyeHeight() * 0.5D, 0.0D, gravityDirection)).z;
    }
}
