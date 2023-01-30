package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;

@Mixin(value = Direction.class, priority = 1001)
public abstract class DirectionMixin {
    @WrapOperation(
            method = "getEntityFacingOrder",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_getEntityFacingOrder_getYaw_0(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(original.call(entity, tickDelta), entity.getPitch(tickDelta), gravityDirection).x;
    }

    @WrapOperation(
            method = "getEntityFacingOrder",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getPitch(F)F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_getEntityFacingOrder_getPitch_0(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), original.call(entity, tickDelta), gravityDirection).y;
    }

    @WrapOperation(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_getLookDirectionForAxis_getYaw_0(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(original.call(entity, tickDelta), entity.getPitch(tickDelta), gravityDirection).x;
    }

    @WrapOperation(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 1
            )
    )
    private static float wrapOperation_getLookDirectionForAxis_getYaw_1(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(original.call(entity, tickDelta), entity.getPitch(tickDelta), gravityDirection).x;
    }

    @WrapOperation(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getPitch(F)F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_getLookDirectionForAxis_getPitch_0(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), original.call(entity, tickDelta), gravityDirection).y;
    }
}
