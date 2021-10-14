package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {
    @Redirect(
            method = "bounceEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_bounceEntity_getVelocity_0(Entity entity) {
        Vec3d vec3d = entity.getVelocity();

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            vec3d = RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
        }

        return vec3d;
    }

    @Redirect(
            method = "bounceEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V",
                    ordinal = 0
            )
    )
    private void redirect_bounceEntity_setVelocity_0(Entity entity, double x, double y, double z) {
        if(!(entity instanceof PlayerEntity)) {
            entity.setVelocity(x, y, z);
            return;
        }
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        entity.setVelocity(RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection));
    }
}
