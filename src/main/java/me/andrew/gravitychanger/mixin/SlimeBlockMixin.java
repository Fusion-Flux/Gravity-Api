package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SlimeBlock.class)
public abstract class SlimeBlockMixin {
    @Redirect(
            method = "bounce",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_bounce_getVelocity(Entity entity) {
        Vec3d vec3d = entity.getVelocity();

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            vec3d = RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
        }

        return vec3d;
    }

    @Redirect(
            method = "bounce",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V",
                    ordinal = 0
            )
    )
    private void redirect_bounce_setVelocity(Entity entity, double x, double y, double z) {
        if(!(entity instanceof PlayerEntity)) {
            entity.setVelocity(x, y, z);
            return;
        }

        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        entity.setVelocity(RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection));
    }

    @Redirect(
            method = "onSteppedOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_onSteppedOn_getVelocity(Entity entity) {
        Vec3d vec3d = entity.getVelocity();

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            vec3d = RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
        }

        return vec3d;
    }

    @Redirect(
            method = "onSteppedOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            )
    )
    private void redirect_onSteepedOn_setVelocity(Entity entity, Vec3d vec3d) {
        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            vec3d = RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
        }

        entity.setVelocity(vec3d);
    }
}
