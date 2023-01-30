package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Direction;

@Mixin(value = Item.class, priority = 1001)
public class ItemMixin {
    @WrapOperation(
            method="raycast",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_raycast_getYaw(PlayerEntity player, Operation<Float> original){
        Direction direction = GravityChangerAPI.getGravityDirection(player);
        if(direction == Direction.DOWN) return original.call(player);
        return RotationUtil.rotPlayerToWorld(original.call(player), player.getPitch(), direction).x;
    }

    @WrapOperation(
            method="raycast",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getPitch()F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_raycast_getPitch(PlayerEntity player, Operation<Float> original){
        Direction direction = GravityChangerAPI.getGravityDirection(player);
        if(direction == Direction.DOWN) return original.call(player);
        return RotationUtil.rotPlayerToWorld(player.getYaw(), original.call(player), direction).y;
    }
}
