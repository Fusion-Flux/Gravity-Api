package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.RamImpactTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RamImpactTask.class)
public abstract class RamImpactTaskMixin {
    @Shadow private Vec3d direction;

    @Redirect(
            method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V",
                    ordinal = 0
            )
    )
    private void redirect_keepRunning_takeKnockback_0(LivingEntity target, double strength, double x, double z) {
        if(!(target instanceof PlayerEntity)) {
            target.takeKnockback(strength, x, z);
            return;
        }
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) target;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        Vec3d direction = RotationUtil.vecWorldToPlayer(this.direction, gravityDirection);
        target.takeKnockback(strength, direction.x, direction.z);
    }
}
