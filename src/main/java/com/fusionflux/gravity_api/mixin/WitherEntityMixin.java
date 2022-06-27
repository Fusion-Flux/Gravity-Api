package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.entity.Entity;
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
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
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
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
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
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getStandingEyeHeight() * 0.5D, 0.0D, gravityDirection)).z;
    }

    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_tickMovement_getEyeY_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return entity.getEyeY();
        }

        return entity.getEyePos().y;
    }

    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_tickMovement_getX_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return entity.getX();
        }

        return entity.getEyePos().x;
    }

    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_tickMovement_getZ_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return entity.getZ();
        }

        return entity.getEyePos().z;
    }
}
