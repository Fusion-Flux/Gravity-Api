package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Redirect(
            method = "onEntityLand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_onEntityLand_getVelocity_0(Entity entity) {
        Vec3d vec3d = entity.getVelocity();

        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            vec3d = RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
        }

        return vec3d;
    }

    @Redirect(
            method = "onEntityLand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            )
    )
    private void redirect_onEntityLand_setVelocity_0(Entity entity, Vec3d vec3d) {
        if(entity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) entity;
            Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

            vec3d = RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
        }

        entity.setVelocity(vec3d);
    }
}
