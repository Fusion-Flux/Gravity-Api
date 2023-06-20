package com.fusionflux.gravity_api.mixin;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin implements CrossbowUser {
    @Redirect(
            method = "shoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/PiglinEntity;shoot(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/projectile/ProjectileEntity;FF)V",
                    ordinal = 0
            )
    )
    private void redirect_shoot_shoot_0(PiglinEntity piglinEntity, LivingEntity entity, LivingEntity target, ProjectileEntity projectile, float multishotSpray, float speed) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN) {
            this.shoot(entity, target, projectile, multishotSpray, speed);
            return;
        }

        Vec3d targetPos = target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getHeight() * 0.3333333333333333D, 0.0D, gravityDirection));

        double d = targetPos.x - entity.getX();
        double e = targetPos.z - entity.getZ();
        double f = Math.sqrt(Math.sqrt(d * d + e * e));
        double g = targetPos.y - projectile.getY() + f * 0.20000000298023224D;
        Vector3f vec3f = this.getProjectileLaunchVelocity(entity, new Vec3d(d, g, e), multishotSpray);
        projectile.setVelocity((double)vec3f.x(), (double)vec3f.y(), (double)vec3f.z(), speed, (float)(14 - entity.getWorld().getDifficulty().getId() * 4));
        entity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
