package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(
            method = "moveToWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_moveToWorld_sendPacket_1(CallbackInfoReturnable<ServerPlayerEntity> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this);
        if(gravityDirection != GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this) && GravityChangerMod.config.resetGravityOnDimensionChange) {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, Direction.DOWN);
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this));
        }
    }

    @Inject(
            method = "teleport",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_teleport_sendPacket_0(CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this);
        if(gravityDirection != GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this) && GravityChangerMod.config.resetGravityOnDimensionChange) {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, Direction.DOWN);
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this));
        }
    }

    @Inject(
            method = "copyFrom",
            at = @At("TAIL")
    )
    private void inject_copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if(GravityChangerMod.config.resetGravityOnRespawn) {
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection(oldPlayer));
        }
    }
}
