package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_onPlayerConnect_sendPacket_0(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        //TODO
        //((ServerPlayerEntityAccessor) player).gravitychanger$sendGravityPacket(GravityChangerAPI.getGravityDirection(player), false);
    }

    // This uses the old player instance but it should be ok as long as the gravity is not changed between new player creation and this
    @Inject(
            method = "respawnPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_respawnPlayer_sendPacket_1(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        //TODO
        //((ServerPlayerEntityAccessor) player).gravitychanger$sendGravityPacket(GravityChangerAPI.getGravityDirection(player), false);
    }
}
