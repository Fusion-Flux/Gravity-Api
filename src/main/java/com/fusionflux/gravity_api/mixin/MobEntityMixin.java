package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Direction;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @WrapOperation(
            method = "tryAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private float wrapOperation_tryAttack_getYaw_0(MobEntity attacker, Operation<Float> original, Entity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN) {
            return original.call(attacker);
        }

        return RotationUtil.rotWorldToPlayer(original.call(attacker), attacker.getPitch(), gravityDirection).x;
    }

    @WrapOperation(
            method = "tryAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;getYaw()F",
                    ordinal = 1
            )
    )
    private float wrapOperation_tryAttack_getYaw_1(MobEntity attacker, Operation<Float> original, Entity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN) {
            return original.call(attacker);
        }

        return RotationUtil.rotWorldToPlayer(original.call(attacker), attacker.getPitch(), gravityDirection).x;
    }

    @Redirect(
            method = "lookAtEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_lookAtEntity_getEyeY_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getEyeY();
        }

        return livingEntity.getEyePos().y;
    }

    @Redirect(
            method = "lookAtEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_lookAtEntity_getX_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return entity.getX();
        }

        return entity.getEyePos().x;
    }

    @Redirect(
            method = "lookAtEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_lookAtEntity_getZ_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return entity.getZ();
        }

        return entity.getEyePos().z;
    }
}
