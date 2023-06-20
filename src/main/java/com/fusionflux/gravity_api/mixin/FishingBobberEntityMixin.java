package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends Entity {



    public FishingBobberEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @WrapOperation(
            method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;refreshPositionAndAngles(DDDFF)V",
                    ordinal = 0
            )
    )
    private void wrapOperation_init_(FishingBobberEntity fishingBobberEntity, double x, double y, double z, float yaw, float pitch, Operation<Void> original, PlayerEntity thrower, World world, int lureLevel, int luckOfTheSeaLevel) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(thrower);
        if(gravityDirection == Direction.DOWN) {
            original.call(fishingBobberEntity, x, y, z, yaw, pitch);
            return;
        }

        Vec3d pos = thrower.getEyePos();
        Vec2f rot = RotationUtil.rotPlayerToWorld(yaw, pitch, gravityDirection);
        original.call(fishingBobberEntity, pos.x, pos.y, pos.z, rot.x, rot.y);
    }

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;II)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private Vec3d modify_init_Vec3d_1(Vec3d vec3d, PlayerEntity thrower, World world, int lureLevel, int luckOfTheSeaLevel) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(thrower);
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = -0.03))
    private double multiplyGravity(double constant) {
        return constant * GravityChangerAPI.getGravityStrength(this);
    }
}
