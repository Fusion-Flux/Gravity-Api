package com.fusionflux.gravity_api.mixin.client;

import java.util.Map;
import java.util.UUID;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 1001)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private Map<UUID, PlayerListEntry> playerListEntries;

    @Redirect(
            method = "onGameStateUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_onGameStateChange_getEyeY_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getEyeY();
        }

        return playerEntity.getEyePos().y;
    }

    @Redirect(
            method = "onGameStateUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_onGameStateChange_getX_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getX();
        }

        return playerEntity.getEyePos().x;
    }

    @Redirect(
            method = "onGameStateUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_onGameStateChange_getZ_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getZ();
        }

        return playerEntity.getEyePos().z;
    }

    @WrapOperation(
            method = "onExplosionOccurs",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d wrapOperation_onExplosion_add_0(Vec3d vec3d, double x, double y, double z, Operation<Vec3d> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(client.player);
        if(gravityDirection == Direction.DOWN) {
            return original.call(vec3d, x, y, z);
        }

        Vec3d player = RotationUtil.vecWorldToPlayer(x, y, z, gravityDirection);
        return original.call(vec3d, player.x, player.y, player.z);
    }
}
