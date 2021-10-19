package me.andrew.gravitychanger.mixin.client;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Redirect(
            method = "onGameStateChange",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_onGameStateChange_getEyeY_0(PlayerEntity playerEntity) {
        Direction gravityDirection = ((EntityAccessor) playerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getEyeY();
        }

        return playerEntity.getEyePos().y;
    }

    @Redirect(
            method = "onGameStateChange",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_onGameStateChange_getX_0(PlayerEntity playerEntity) {
        Direction gravityDirection = ((EntityAccessor) playerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getX();
        }

        return playerEntity.getEyePos().x;
    }

    @Redirect(
            method = "onGameStateChange",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_onGameStateChange_getZ_0(PlayerEntity playerEntity) {
        Direction gravityDirection = ((EntityAccessor) playerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getZ();
        }

        return playerEntity.getEyePos().z;
    }
}
