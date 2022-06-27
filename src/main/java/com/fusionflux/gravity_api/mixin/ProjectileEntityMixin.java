package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @ModifyVariable(
            method = "setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V",
            at = @At("HEAD"),
            ordinal = 0
    )
    private float modify_setProperties_pitch(float value, Entity user, float yaw, float roll, float speed, float divergence) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(user);
        if(gravityDirection == Direction.DOWN) {
            return value;
        }

        return RotationUtil.rotPlayerToWorld(user.getYaw(), user.getPitch(), gravityDirection).y;
    }

    @ModifyVariable(
            method = "setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V",
            at = @At("HEAD"),
            ordinal = 1
    )
    private float modify_setProperties_yaw(float value, Entity user, float pitch, float roll, float speed, float divergence) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(user);
        if(gravityDirection == Direction.DOWN) {
            return value;
        }

        return RotationUtil.rotPlayerToWorld(user.getYaw(), user.getPitch(), gravityDirection).x;
    }
}
