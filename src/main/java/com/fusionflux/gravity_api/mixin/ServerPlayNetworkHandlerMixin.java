package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(serverPlayerEntity);
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
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(serverPlayerEntity);
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
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
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
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
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
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    //@Redirect(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getY()D",
    //                ordinal = 0
    //        )
    //)
    //private double redirect_onVehicleMove_getY_0(Entity instance) {
    //    Direction gravityDirection = ((EntityAccessor) instance).gravitychanger$getAppliedGravityDirection();
    //    if(gravityDirection == Direction.DOWN) {
    //        return instance.getY();
    //    }
//
    //    return RotationUtil.vecWorldToPlayer(instance.getPos(), gravityDirection).y;
    //}
//
    //@Redirect(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getY()D",
    //                ordinal = 2
    //        )
    //)
    //private double redirect_onVehicleMove_getY_2(Entity instance) {
    //    Direction gravityDirection = ((EntityAccessor) instance).gravitychanger$getAppliedGravityDirection();
    //    if(gravityDirection == Direction.DOWN) {
    //        return instance.getY();
    //    }
//
    //    return RotationUtil.vecWorldToPlayer(instance.getPos(), gravityDirection).y;
    //}

    @ModifyArg(
            method = "onVehicleMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
            ),
            index = 1
    )
    private Vec3d modify_onVehicleMove_move_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    //@ModifyVariable(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getX()D",
    //                ordinal = 1
    //        ),ordinal = 0
    //)
    //private double modify_onVehicleMove_double_12(double value) {
    //    Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
    //    if(gravityDirection == Direction.DOWN) {
    //        return value;
    //    }
//
    //    return gravitychanger$onPlayerMove_playerMovementY;
    //}


    @ModifyArgs(
            method = "isEntityOnAir",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"
            )
    )
    private void modify_onVehicleMove_move_0(Args args) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        Vec3d argVec = new Vec3d(args.get(0),args.get(1),args.get(2));
        argVec = RotationUtil.vecWorldToPlayer(argVec, gravityDirection);

        args.set(0,argVec.x);
        args.set(1,argVec.y);
        args.set(2,argVec.z);

    }
    @ModifyArgs(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;handleFall(DDDZ)V",
                    ordinal = 0
            )
    )
    private void modify_onPlayerMove_handleFall_0(Args args) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        Vec3d argVec = new Vec3d(args.get(0), args.get(1), args.get(2));
        argVec = RotationUtil.vecWorldToPlayer(argVec, gravityDirection);
        args.set(0,argVec.x);
        args.set(1,argVec.y);
        args.set(2,argVec.z);

    }

    double testvaluey =0;
    @Inject(
            method = "onPlayerMove",
            at = @At(
                    value = "HEAD"
            )
    )
    private void test(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        testvaluey = this.player.getY();
    }
    @ModifyArgs(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;handleFall(DDDZ)V",
                    ordinal = 1
            )
    )
    private void modify_onPlayerMove_handleFall_1(Args args) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        Vec3d argVec = new Vec3d(args.get(0), this.player.getY()-testvaluey, args.get(2));
        argVec = RotationUtil.vecWorldToPlayer(argVec, gravityDirection);
        args.set(0,argVec.x);
        args.set(1,argVec.y);
        args.set(2,argVec.z);

    }
}
