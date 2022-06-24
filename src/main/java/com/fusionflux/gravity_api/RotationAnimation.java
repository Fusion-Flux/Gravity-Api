package com.fusionflux.gravity_api;

import com.fusionflux.gravity_api.util.QuaternionUtil;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.Validate;

@Environment(EnvType.CLIENT)
public class RotationAnimation {
    private static boolean inAnimation = false;
    private static Quaternion startGravityRotation;
    private static Quaternion endGravityRotation;
    private static long startTimeMs;
    private static long endTimeMs;
    
    public static void applyRotationAnimation(Direction newGravity, Direction prevGravity, int durationTimeMs) {
        if (durationTimeMs == 0) {
            inAnimation = false;
            return;
        }
        
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        Validate.notNull(player);
        
        Vec3d newLookingDirection = getNewLookingDirection(newGravity, prevGravity, player);
        
        Quaternion oldViewRotation = QuaternionUtil.getViewRotation(player.getPitch(), player.getYaw());
        
        Quaternion currentAnimatedGravityRotation = getCurrentGravityRotation(prevGravity);
        
        // camera rotation = view rotation(pitch and yaw) * gravity rotation(animated)
        Quaternion currentAnimatedCameraRotation = QuaternionUtil.mult(oldViewRotation, currentAnimatedGravityRotation);
        
        Quaternion newEndGravityRotation = RotationUtil.getWorldRotationQuaternion(newGravity);
        
        Vec2f newYawAndPitch = RotationUtil.vecToRot(
            RotationUtil.vecWorldToPlayer(newLookingDirection, newGravity)
        );
        float newPitch = newYawAndPitch.y;
        float newYaw = newYawAndPitch.x;
        float deltaYaw = newYaw-player.getYaw();
        float deltaPitch = newPitch-player.getPitch();
        player.setYaw(player.getYaw()+deltaYaw);
        player.setPitch(player.getPitch()+deltaPitch);
        player.prevYaw += deltaYaw;
        player.prevPitch += deltaPitch;
        player.bodyYaw += deltaYaw;
        player.prevBodyYaw += deltaYaw;
        player.headYaw += deltaYaw;
        player.prevHeadYaw += deltaYaw;
        
        Quaternion newViewRotation = QuaternionUtil.getViewRotation(player.getPitch(), player.getYaw());
        
        // gravity rotation = (view rotation^-1) * camera rotation
        Quaternion animationStartGravityRotation = QuaternionUtil.mult(
            QuaternionUtil.inversed(newViewRotation), currentAnimatedCameraRotation
        );
        
        long now = getTimeMs();
        inAnimation = true;
        startGravityRotation = animationStartGravityRotation;
        endGravityRotation = newEndGravityRotation;
        startTimeMs = now;
        endTimeMs = now + durationTimeMs;
    }
    
    private static Vec3d getNewLookingDirection(
        Direction newGravity, Direction prevGravity, ClientPlayerEntity player
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
    
    private static long getTimeMs() {
        return System.currentTimeMillis();
    }
    
    public static Quaternion getCurrentGravityRotation(Direction currentGravity) {
        
        long now = getTimeMs();
        
        if (now > endTimeMs) {
            inAnimation = false;
        }
        
        if (!inAnimation) {
            return RotationUtil.getWorldRotationQuaternion(currentGravity);
        }
        
        double delta = ((double) (now - startTimeMs)) / (endTimeMs - startTimeMs);
        
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
    
    public static boolean isInAnimation() {
        return inAnimation;
    }
}
