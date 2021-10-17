package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Redirect(
            method = "tryAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private float redirect_tryAttack_getYaw_0(MobEntity attacker, Entity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return attacker.getYaw();
        }

        return RotationUtil.rotWorldToPlayer(attacker.getYaw(), attacker.getPitch(), gravityDirection).x;
    }

    @Redirect(
            method = "tryAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;getYaw()F",
                    ordinal = 1
            )
    )
    private float redirect_tryAttack_getYaw_1(MobEntity attacker, Entity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return attacker.getYaw();
        }

        return RotationUtil.rotWorldToPlayer(attacker.getYaw(), attacker.getPitch(), gravityDirection).x;
    }
}
