package com.fusionflux.fusions_gravity_api.mixin;

import com.fusionflux.fusions_gravity_api.accessor.EntityAccessor;
import com.fusionflux.fusions_gravity_api.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_collectBlocksAndDamageEntities_getEyeY_0(Entity entity) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getEyeY();
        }

        return entity.getEyePos().y;
    }

    @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_collectBlocksAndDamageEntities_getX_0(Entity entity) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getX();
        }

        return entity.getEyePos().x;
    }

    @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_collectBlocksAndDamageEntities_getZ_0(Entity entity) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getZ();
        }

        return entity.getEyePos().z;
    }

    @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_collectBlocksAndDamageEntities_getVelocity_0(Entity entity) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getVelocity();
        }

        return RotationUtil.vecPlayerToWorld(entity.getVelocity(), gravityDirection);
    }

    @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            )
    )
    private void redirect_collectBlocksAndDamageEntities_setVelocity_0(Entity entity, Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            entity.setVelocity(vec3d);
            return;
        }

        entity.setVelocity(RotationUtil.vecWorldToPlayer(vec3d, gravityDirection));
    }
}
