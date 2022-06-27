package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WolfBegGoal.class)
public abstract class WolfBegGoalMixin {
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getEyeY_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getEyeY();
        }

        return playerEntity.getEyePos().y;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getX_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getX();
        }

        return playerEntity.getEyePos().x;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getZ_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getZ();
        }

        return playerEntity.getEyePos().z;
    }
}
