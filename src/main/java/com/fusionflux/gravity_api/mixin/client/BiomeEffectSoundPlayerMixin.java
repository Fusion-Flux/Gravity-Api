package com.fusionflux.gravity_api.mixin.client;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//method_26271 refers to a lambda which is why this class may cause mixin warnings/errors
@Mixin(BiomeEffectSoundPlayer.class)
public abstract class BiomeEffectSoundPlayerMixin {
    @Redirect(
            method = {"m_hhelwrkg","method_26271"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_method_26271_getEyeY_0(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getEyeY();
        }

        return clientPlayerEntity.getEyePos().y;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_method_26271_getX_0(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getX();
        }

        return clientPlayerEntity.getEyePos().x;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_method_26271_getZ_0(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getZ();
        }

        return clientPlayerEntity.getEyePos().z;
    }

    @Redirect(
            method = {"m_hhelwrkg","method_26271"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEyeY()D",
                    ordinal = 1
            )
    )
    private double redirect_method_26271_getEyeY_1(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getEyeY();
        }

        return clientPlayerEntity.getEyePos().y;
    }

    @Redirect(
            method = {"m_hhelwrkg","method_26271"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 1
            )
    )
    private double redirect_method_26271_getX_1(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getX();
        }

        return clientPlayerEntity.getEyePos().x;
    }

    @Redirect(
            method = {"m_hhelwrkg","method_26271"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 1
            )
    )
    private double redirect_method_26271_getZ_1(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getZ();
        }

        return clientPlayerEntity.getEyePos().z;
    }

    @Redirect(
            method = {"m_hhelwrkg","method_26271"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEyeY()D",
                    ordinal = 2
            )
    )
    private double redirect_method_26271_getEyeY_2(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getEyeY();
        }

        return clientPlayerEntity.getEyePos().y;
    }

    @Redirect(
            method = {"m_hhelwrkg","method_26271"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D",
                    ordinal = 2
            )
    )
    private double redirect_method_26271_getX_2(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getX();
        }

        return clientPlayerEntity.getEyePos().x;
    }

    @Redirect(
            method = {"m_hhelwrkg","method_26271"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D",
                    ordinal = 2
            )
    )
    private double redirect_method_26271_getZ_2(ClientPlayerEntity clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if(gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getZ();
        }

        return clientPlayerEntity.getEyePos().z;
    }
}
