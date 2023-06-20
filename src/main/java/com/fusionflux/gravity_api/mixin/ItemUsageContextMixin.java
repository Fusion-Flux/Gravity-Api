package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.Direction;

@Mixin(ItemUsageContext.class)
public abstract class ItemUsageContextMixin {
    @WrapOperation(
            method = "getPlayerYaw",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private float wrapOperation_getPlayerYaw_getYaw_0(PlayerEntity entity, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity);
        }

        return RotationUtil.rotPlayerToWorld(original.call(entity), entity.getPitch(), gravityDirection).x;
    }
}
