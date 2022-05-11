package me.andrew.gravitychanger.mixin.client;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
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
        if(this.camera.getFocusedEntity() != null) {
            Entity focusedEntity = this.camera.getFocusedEntity();
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
            matrix.multiply(RotationUtil.getRotation(gravityDirection));
        }
       // float accuTicks = GravityChangerAPI.getAccumulatedTicks(focusedEntity);
       // if(accuTicks <=1) {
       //     Direction gravityDirection = ((EntityAccessor) this.camera.getFocusedEntity()).gravitychanger$getAppliedGravityDirection();
       //     Direction prevGravityDirection = GravityChangerAPI.getPrevGravityDirection(this.camera.getFocusedEntity());
       //     matrix.multiply(RotationUtil.interpolate(RotationUtil.getWorldRotationQuaternion(prevGravityDirection), RotationUtil.getWorldRotationQuaternion(gravityDirection), accuTicks ));
       //     GravityChangerAPI.setAccumulatedTicks(focusedEntity,accuTicks+(tickDelta*.25f));
       // }else{
       //     Direction gravityDirection = ((EntityAccessor) this.camera.getFocusedEntity()).gravitychanger$getAppliedGravityDirection();
       //     matrix.multiply(RotationUtil.getWorldRotationQuaternion(gravityDirection));
       // }
    }
}
