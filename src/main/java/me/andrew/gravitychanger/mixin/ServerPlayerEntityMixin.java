package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.GravityChangerMod;
import me.andrew.gravitychanger.accessor.ServerPlayerEntityAccessor;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.data.TrackedData;
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
public abstract class ServerPlayerEntityMixin implements  ServerPlayerEntityAccessor {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Override
    public void gravitychanger$sendGravityPacket(Direction gravityDirection, boolean initialGravity) {
        if(this.networkHandler == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(gravityDirection);
        buf.writeBoolean(initialGravity);
        this.networkHandler.sendPacket(new CustomPayloadS2CPacket(GravityChangerMod.CHANNEL_GRAVITY, buf));
    }

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
            RotationUtil.applyNewRotation(gravityDirection,GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this),GravityChangerMod.config.rotationTime);
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this));
            RotationUtil.applyNewRotation(gravityDirection,GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this),GravityChangerMod.config.rotationTime);
            this.gravitychanger$sendGravityPacket(gravityDirection, false);
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
            RotationUtil.applyNewRotation(gravityDirection,GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this),GravityChangerMod.config.rotationTime);
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this));
            RotationUtil.applyNewRotation(gravityDirection,GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this),GravityChangerMod.config.rotationTime);
            this.gravitychanger$sendGravityPacket(gravityDirection, false);
        }
    }

    @Inject(
            method = "copyFrom",
            at = @At("TAIL")
    )
    private void inject_copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if(GravityChangerMod.config.resetGravityOnRespawn) {
            RotationUtil.applyNewRotation(GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this),GravityChangerAPI.getDefaultGravityDirection((ServerPlayerEntity)(Object)this),GravityChangerMod.config.rotationTime);
            //GravityChangerAPI.updateGravity(oldPlayer);
            //GravityChangerAPI.setGravityDirection(oldPlayer, GravityChangerAPI.getDefaultGravityDirection(oldPlayer));
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayerEntity)(Object)this, GravityChangerAPI.getDefaultGravityDirection(oldPlayer));
            RotationUtil.applyNewRotation(GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this),GravityChangerAPI.getGravityDirection((ServerPlayerEntity)(Object)this),GravityChangerMod.config.rotationTime);
        }
    }
}
