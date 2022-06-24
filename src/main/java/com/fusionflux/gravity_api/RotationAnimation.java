package com.fusionflux.gravity_api;

import com.fusionflux.gravity_api.util.RotationUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

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
        
        long now = getTimeMs();
        
        Quaternion currentGravityRotation = getCurrentGravityRotation(prevGravity);
        
        Quaternion targetGravityRotation = RotationUtil.getWorldRotationQuaternion(newGravity);
        
        inAnimation = true;
        startGravityRotation = currentGravityRotation;
        endGravityRotation = targetGravityRotation;
        startTimeMs = now;
        endTimeMs = now + durationTimeMs;
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
}
