package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Redirect(
            method = "processBlockBreakingAction",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_processBlockBreakingAction_getX_0(ServerPlayerEntity serverPlayerEntity) {
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) serverPlayerEntity;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        return serverPlayerEntity.getX() + RotationUtil.vecPlayerToWorld(0.0D, 1.5D, 0.0D, gravityDirection).x;
    }

    @Redirect(
            method = "processBlockBreakingAction",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getY()D",
                    ordinal = 0
            )
    )
    private double redirect_processBlockBreakingAction_getY_0(ServerPlayerEntity serverPlayerEntity) {
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) serverPlayerEntity;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        return serverPlayerEntity.getY() - 1.5D + RotationUtil.vecPlayerToWorld(0.0D, 1.5D, 0.0D, gravityDirection).y;
    }

    @Redirect(
            method = "processBlockBreakingAction",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_processBlockBreakingAction_getZ_0(ServerPlayerEntity serverPlayerEntity) {
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) serverPlayerEntity;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        return serverPlayerEntity.getZ() + RotationUtil.vecPlayerToWorld(0.0D, 1.5D, 0.0D, gravityDirection).z;
    }
}
