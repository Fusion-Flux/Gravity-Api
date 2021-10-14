package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @ModifyVariable(
            method = "setProperties",
            at = @At("HEAD"),
            ordinal = 0
    )
    private float modify_setProperties_pitch(float pitchUnused, Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        if(!(user instanceof PlayerEntity)) return pitchUnused;
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) user;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        return RotationUtil.rotPlayerToWorld(user.getYaw(), user.getPitch(), gravityDirection).y;
    }

    @ModifyVariable(
            method = "setProperties",
            at = @At("HEAD"),
            ordinal = 1
    )
    private float modify_setProperties_yaw(float yawUnused, Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        if(!(user instanceof PlayerEntity)) return yawUnused;
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) user;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        return RotationUtil.rotPlayerToWorld(user.getYaw(), user.getPitch(), gravityDirection).x;
    }
}
