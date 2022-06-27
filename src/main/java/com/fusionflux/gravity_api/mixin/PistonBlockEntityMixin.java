package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin {
    @Redirect(
            method = "moveEntity",
            at = @At(
                    value = "NEW",
                    target = "(DDD)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private static Vec3d redirect_moveEntity_Vec3d_0(double x, double y, double z, Direction direction, Entity entity, double d, Direction direction2) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return new Vec3d(x, y, z);
        }

        return RotationUtil.vecWorldToPlayer(x, y, z, gravityDirection);
    }
}
