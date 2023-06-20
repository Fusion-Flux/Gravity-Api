package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
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
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_moveToWorld_sendPacket_1(CallbackInfoReturnable<ServerPlayerEntity> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this);
        if(gravityDirection != GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this) && GravityChangerMod.config.resetGravityOnDimensionChange) {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, Direction.DOWN, new RotationParameters().rotationTime(0));
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this), new RotationParameters().rotationTime(0));
        }
    }

    @Inject(
            method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_teleport_sendPacket_0(CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this);
        if(gravityDirection != GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this) && GravityChangerMod.config.resetGravityOnDimensionChange) {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, Direction.DOWN, new RotationParameters().rotationTime(0));
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this), new RotationParameters().rotationTime(0));
        }
    }

    //@Inject(
    //        method = "copyFrom",
    //        at = @At("TAIL")
    //)
    //private void inject_copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
    //    if(GravityChangerConfig.resetGravityOnRespawn) {
    //        GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, Direction.DOWN, new RotationParameters().rotationTime(0));
    //    } else {
    //        GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection(oldPlayer), new RotationParameters().rotationTime(0));
    //    }
    //}
}
