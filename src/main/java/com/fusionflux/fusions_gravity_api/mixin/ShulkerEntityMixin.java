package com.fusionflux.fusions_gravity_api.mixin;

import com.fusionflux.fusions_gravity_api.accessor.EntityAccessor;
import com.fusionflux.fusions_gravity_api.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerEntity.class)
public abstract class ShulkerEntityMixin {
    @Redirect(
            method = "moveEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            )
    )
    private void redirect_pushEntities_move_0(Entity entity, MovementType movementType, Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            entity.move(movementType, vec3d);
            return;
        }

        entity.move(movementType, RotationUtil.vecWorldToPlayer(vec3d, gravityDirection));
    }
}
