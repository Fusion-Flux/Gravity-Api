package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        targets = "net/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal"
)
public abstract class EndermanEntity$ChasePlayerGoalMixin {
    @Redirect(
            method = "Lnet/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal;tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getEyeY_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getEyeY();
        }

        return livingEntity.getEyePos().y;
    }

    @Redirect(
            method = "Lnet/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal;tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getX_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getX();
        }

        return livingEntity.getEyePos().x;
    }

    @Redirect(
            method = "Lnet/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal;tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getZ_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getZ();
        }

        return livingEntity.getEyePos().z;
    }
}
