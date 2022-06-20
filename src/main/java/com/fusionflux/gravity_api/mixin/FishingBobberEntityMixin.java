package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.accessor.EntityAccessor;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends Entity implements EntityAccessor {



    public FishingBobberEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        Entity vehicle = this.getVehicle();
        if(vehicle != null) {
            return ((EntityAccessor) vehicle).gravitychanger$getAppliedGravityDirection();
        }

        return GravityChangerAPI.getGravityDirection((FishingBobberEntity)(Object)this);
    }

/*
    @ModifyVariable(
            method = "tick",
            at = @At(
                    value = "STORE"
            )
            ,ordinal = 0
    )
    public Vec3d tick(Vec3d modify){
        modify = new Vec3d(modify.x, modify.y+0.05, modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify,gravitychanger$getGravityDirection());
        modify = new Vec3d(modify.x, modify.y-0.05, modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify,gravitychanger$getGravityDirection());
        return  modify;
    }*/

    @Redirect(
            method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;refreshPositionAndAngles(DDDFF)V",
                    ordinal = 0
            )
    )
    private void redirect_init_(FishingBobberEntity fishingBobberEntity, double x, double y, double z, float yaw, float pitch, PlayerEntity thrower, World world, int lureLevel, int luckOfTheSeaLevel) {
        Direction gravityDirection = ((EntityAccessor) thrower).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            fishingBobberEntity.refreshPositionAndAngles(x, y, z, yaw, pitch);
            return;
        }

        Vec3d pos = thrower.getEyePos();
        Vec2f rot = RotationUtil.rotPlayerToWorld(yaw, pitch, gravityDirection);
        fishingBobberEntity.refreshPositionAndAngles(pos.x, pos.y, pos.z, rot.x, rot.y);
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
        Direction gravityDirection = ((EntityAccessor) thrower).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }
}
