package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
        float yaw = entity.getYaw(tickDelta);

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            yaw = RotationUtil.rotPlayerToWorld(yaw, entity.getPitch(tickDelta), gravityDirection).x;
        }

        return yaw;
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
        float pitch = entity.getPitch(tickDelta);

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            pitch = RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), pitch, gravityDirection).y;
        }

        return pitch;
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
        float yaw = entity.getYaw(tickDelta);

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            yaw = RotationUtil.rotPlayerToWorld(yaw, entity.getPitch(tickDelta), gravityDirection).x;
        }

        return yaw;
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
        float yaw = entity.getYaw(tickDelta);

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            yaw = RotationUtil.rotPlayerToWorld(yaw, entity.getPitch(tickDelta), gravityDirection).x;
        }

        return yaw;
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
        float pitch = entity.getPitch(tickDelta);

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            pitch = RotationUtil.rotPlayerToWorld(entity.getYaw(tickDelta), pitch, gravityDirection).y;
        }

        return pitch;
    }
}
