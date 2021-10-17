package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
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
        Direction gravityDirection = ((EntityAccessor) serverPlayerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return serverPlayerEntity.getX();
        }

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
        Direction gravityDirection = ((EntityAccessor) serverPlayerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return serverPlayerEntity.getY();
        }

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
        Direction gravityDirection = ((EntityAccessor) serverPlayerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return serverPlayerEntity.getZ();
        }

        return serverPlayerEntity.getZ() + RotationUtil.vecPlayerToWorld(0.0D, 1.5D, 0.0D, gravityDirection).z;
    }
}
