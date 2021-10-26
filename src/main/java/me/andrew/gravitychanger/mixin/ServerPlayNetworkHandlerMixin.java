package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    private static double gravitychanger$onPlayerMove_playerMovementY;

    @Shadow public ServerPlayerEntity player;

    @Shadow private static double clampHorizontal(double d) { return 0; };

    @Shadow private static double clampVertical(double d) { return 0; };

    @Shadow private double updatedX;

    @Shadow private double updatedY;

    @Shadow private double updatedZ;

    @Redirect(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getY()D",
                    ordinal = 3
            )
    )
    private double redirect_onPlayerMove_getY_3(ServerPlayerEntity serverPlayerEntity) {
        Direction gravityDirection = ((EntityAccessor) serverPlayerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return serverPlayerEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(serverPlayerEntity.getPos(), gravityDirection).y;
    }

    @Redirect(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getY()D",
                    ordinal = 7
            )
    )
    private double redirect_onPlayerMove_getY_7(ServerPlayerEntity serverPlayerEntity) {
        Direction gravityDirection = ((EntityAccessor) serverPlayerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return serverPlayerEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(serverPlayerEntity.getPos(), gravityDirection).y;
    }

    @ModifyVariable(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;isOnGround()Z",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private boolean modify_onPlayerMove_boolean_0(boolean value, PlayerMoveC2SPacket packet) {
        Direction gravityDirection = ((EntityAccessor) this.player).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return value;
        }

        gravitychanger$onPlayerMove_playerMovementY = RotationUtil.vecWorldToPlayer(
                clampHorizontal(packet.getX(this.player.getX())) - this.updatedX,
                clampVertical(packet.getY(this.player.getY())) - this.updatedY,
                clampHorizontal(packet.getZ(this.player.getZ())) - this.updatedZ,
                gravityDirection
        ).y;
        return gravitychanger$onPlayerMove_playerMovementY > 0.0D;
    }

    @ModifyVariable(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D",
                    ordinal = 5
            ),
            ordinal = 10
    )
    private double modify_onPlayerMove_double_12(double value) {
        Direction gravityDirection = ((EntityAccessor) this.player).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return value;
        }

        return gravitychanger$onPlayerMove_playerMovementY;
    }

    @ModifyArg(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private Vec3d modify_onPlayerMove_move_0(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this.player).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
}
