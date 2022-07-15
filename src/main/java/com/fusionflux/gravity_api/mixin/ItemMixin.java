package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public class ItemMixin {
    @Redirect(
            method="raycast",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private static float redirect_raycast_getYaw(PlayerEntity player){
        Direction direction = GravityChangerAPI.getGravityDirection(player);
        if(direction == Direction.DOWN) return player.getYaw();
        return RotationUtil.rotPlayerToWorld(player.getYaw(), player.getPitch(), direction).x;
    }

    @Redirect(
            method="raycast",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getPitch()F",
                    ordinal = 0
            )
    )
    private static float redirect_raycast_getPitch(PlayerEntity player){
        Direction direction = GravityChangerAPI.getGravityDirection(player);
        if(direction == Direction.DOWN) return player.getPitch();
        return RotationUtil.rotPlayerToWorld(player.getYaw(), player.getPitch(), direction).y;
    }
}
