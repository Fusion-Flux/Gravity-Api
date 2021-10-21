package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
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
public abstract class FishingBobberEntityMixin {
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

        Vec3d pos = thrower.getEyePos().add(RotationUtil.vecPlayerToWorld(x - thrower.getX(), y - thrower.getEyeY(), z - thrower.getZ(), gravityDirection));
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
