package com.fusionflux.gravity_api;

import com.fusionflux.gravity_api.util.QuaternionUtil;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import org.apache.commons.lang3.Validate;

public class RotationAnimation {
    private boolean inAnimation = false;
    private Quaternion startGravityRotation;
    private Quaternion endGravityRotation;
    private long startTimeMs;
    private long endTimeMs;
    
    public void applyRotationAnimation(Direction newGravity, Direction prevGravity, long durationTimeMs, Entity entity, long timeMs) {
        if (durationTimeMs == 0) {
            inAnimation = false;
            return;
        }

        Validate.notNull(entity);
        
        Vec3d newLookingDirection = getNewLookingDirection(newGravity, prevGravity, entity);
        
        Quaternion oldViewRotation = QuaternionUtil.getViewRotation(entity.getPitch(), entity.getYaw());
        
        Quaternion currentAnimatedGravityRotation = getCurrentGravityRotation(prevGravity, timeMs);
        
        // camera rotation = view rotation(pitch and yaw) * gravity rotation(animated)
        Quaternion currentAnimatedCameraRotation = QuaternionUtil.mult(oldViewRotation, currentAnimatedGravityRotation);
        
        Quaternion newEndGravityRotation = RotationUtil.getWorldRotationQuaternion(newGravity);
        
        Vec2f newYawAndPitch = RotationUtil.vecToRot(
            RotationUtil.vecWorldToPlayer(newLookingDirection, newGravity)
        );
        float newPitch = newYawAndPitch.y;
        float newYaw = newYawAndPitch.x;
        float deltaYaw = newYaw- entity.getYaw();
        float deltaPitch = newPitch- entity.getPitch();
        entity.setYaw(entity.getYaw()+deltaYaw);
        entity.setPitch(entity.getPitch()+deltaPitch);
        entity.prevYaw += deltaYaw;
        entity.prevPitch += deltaPitch;
        if(entity instanceof LivingEntity livingEntity) {
            livingEntity.bodyYaw += deltaYaw;
            livingEntity.prevBodyYaw += deltaYaw;
            livingEntity.headYaw += deltaYaw;
            livingEntity.prevHeadYaw += deltaYaw;
        }
        
        Quaternion newViewRotation = QuaternionUtil.getViewRotation(entity.getPitch(), entity.getYaw());
        
        // gravity rotation = (view rotation^-1) * camera rotation
        Quaternion animationStartGravityRotation = QuaternionUtil.mult(
            QuaternionUtil.inversed(newViewRotation), currentAnimatedCameraRotation
        );

        inAnimation = true;
        startGravityRotation = animationStartGravityRotation;
        endGravityRotation = newEndGravityRotation;
        startTimeMs = timeMs;
        endTimeMs = timeMs + durationTimeMs;
    }
    
    private Vec3d getNewLookingDirection(
        Direction newGravity, Direction prevGravity, Entity player
    ) {
        Vec3d oldLookingDirection = RotationUtil.vecPlayerToWorld(
            RotationUtil.rotToVec(player.getYaw(), player.getPitch()),
            prevGravity
        );
        
        if (newGravity == prevGravity.getOpposite()) {
            return oldLookingDirection.multiply(-1);
        }
        
        Quaternion deltaRotation = QuaternionUtil.getRotationBetween(
            Vec3d.of(prevGravity.getVector()),
            Vec3d.of(newGravity.getVector())
        );
        
        Vec3f lookingDirection = new Vec3f(oldLookingDirection);
        lookingDirection.rotate(deltaRotation);
        Vec3d newLookingDirection = new Vec3d(lookingDirection);
        return newLookingDirection;
    }
    
    public Quaternion getCurrentGravityRotation(Direction currentGravity, long timeMs) {
        
        if (timeMs > endTimeMs) {
            inAnimation = false;
        }
        
        if (!inAnimation) {
            return RotationUtil.getWorldRotationQuaternion(currentGravity);
        }
        
        double delta = (double)(timeMs - startTimeMs) / (endTimeMs - startTimeMs);
        
        // make sure that frustum culling updates when running rotation animation
        MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
        
        return RotationUtil.interpolate(
            startGravityRotation, endGravityRotation,
            mapProgress((float) delta)
        );
    }
    
    private static float mapProgress(float delta) {
        return MathHelper.clamp((delta * delta * (3 - 2 * delta)), 0, 1);
    }
    
    public boolean isInAnimation() {
        return inAnimation;
    }

    public void toNbt(NbtCompound nbt) {
        nbt.putBoolean("InAnimation", inAnimation);
        if(inAnimation) {
            nbt.putFloat("Q0X", startGravityRotation.getX());
            nbt.putFloat("Q0Y", startGravityRotation.getY());
            nbt.putFloat("Q0Z", startGravityRotation.getZ());
            nbt.putFloat("Q0W", startGravityRotation.getW());
            nbt.putFloat("Q1X", endGravityRotation.getX());
            nbt.putFloat("Q1Y", endGravityRotation.getY());
            nbt.putFloat("Q1Z", endGravityRotation.getZ());
            nbt.putFloat("Q1W", endGravityRotation.getW());
            nbt.putLong("StartTime", startTimeMs);
            nbt.putLong("EndTime", endTimeMs);
        }
    }

    public void fromNbt(NbtCompound nbt) {
        inAnimation = nbt.getBoolean("InAnimation");//Will return false if no such element exists
        if(inAnimation){
            startGravityRotation = new Quaternion(
                    nbt.getFloat("Q0X"),
                    nbt.getFloat("Q0Y"),
                    nbt.getFloat("Q0Z"),
                    nbt.getFloat("Q0W")
            );
            endGravityRotation = new Quaternion(
                    nbt.getFloat("Q1X"),
                    nbt.getFloat("Q1Y"),
                    nbt.getFloat("Q1Z"),
                    nbt.getFloat("Q1W")
            );
            startTimeMs = nbt.getLong("StartTime");
            endTimeMs = nbt.getLong("EndTime");
        }
    }
}
