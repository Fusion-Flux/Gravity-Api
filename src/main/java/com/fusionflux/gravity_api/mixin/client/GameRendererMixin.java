package com.fusionflux.gravity_api.mixin.client;

import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Camera camera;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (this.camera.getFocusedEntity() != null) {
            Entity focusedEntity = this.camera.getFocusedEntity();
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
            Quaternion currentGravityRotation = RotationAnimation.getCurrentGravityRotation(gravityDirection);
            matrix.multiply(currentGravityRotation);
        }
    }
}
