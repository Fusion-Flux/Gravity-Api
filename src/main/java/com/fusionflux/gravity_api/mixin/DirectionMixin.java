package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.accessor.EntityAccessor;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Direction.class)
public abstract class DirectionMixin {
    @Redirect(
            method = "getEntityFacingOrder",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 0
            )
    )
    private static float redirect_getEntityFacingOrder_getYaw_0(Entity entity, float tickDelta) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getYaw(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), entity.getPitch(tickDelta), gravityDirection).x;
    }

    @Redirect(
            method = "getEntityFacingOrder",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getPitch(F)F",
                    ordinal = 0
            )
    )
    private static float redirect_getEntityFacingOrder_getPitch_0(Entity entity, float tickDelta) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getPitch(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), entity.getPitch(tickDelta), gravityDirection).y;
    }

    @Redirect(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 0
            )
    )
    private static float redirect_getLookDirectionForAxis_getYaw_0(Entity entity, float tickDelta) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getYaw(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), entity.getPitch(tickDelta), gravityDirection).x;
    }

    @Redirect(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 1
            )
    )
    private static float redirect_getLookDirectionForAxis_getYaw_1(Entity entity, float tickDelta) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getYaw(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), entity.getPitch(tickDelta), gravityDirection).x;
    }

    @Redirect(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getPitch(F)F",
                    ordinal = 0
            )
    )
    private static float redirect_getLookDirectionForAxis_getPitch_0(Entity entity, float tickDelta) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getPitch(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), entity.getPitch(tickDelta), gravityDirection).y;
    }
}
