package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Mixin(value = ShulkerBoxBlockEntity.class, priority = 1001)
public abstract class ShulkerBoxBlockEntityMixin {
    @WrapOperation(
            method = "pushEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            )
    )
    private void wrapOperation_pushEntities_move_0(Entity entity, MovementType movementType, Vec3d vec3d, Operation<Void> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            original.call(entity, movementType, vec3d);
            return;
        }

        original.call(entity, movementType, RotationUtil.vecWorldToPlayer(vec3d, gravityDirection));
    }
}
