package com.fusionflux.gravity_api.mixin.client;

import java.util.Optional;

import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.CompatMath;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

@Mixin(value = Camera.class, priority = 1001)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(double x, double y, double z);

    @Shadow private Entity focusedEntity;

    @Shadow @Final private Quaternionf rotation;
    
    @Shadow private float lastCameraY;
    
    @Shadow private float cameraY;

    private float storedTickDelta = 0.f;

    @Inject(method="update", at=@At("HEAD"))
    private void inject_update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci){
        storedTickDelta = tickDelta;
    }

    @WrapOperation(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V",
                    ordinal = 0
            )
    )
    private void wrapOperation_update_setPos_0(Camera camera, double x, double y, double z, Operation<Void> original, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);;
        Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(focusedEntity);
        if(animationOptional.isEmpty()){
            original.call(this, x, y, z);
            return;
        }
        RotationAnimation animation = animationOptional.get();
        if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
            original.call(this, x, y, z);
            return;
        }
        long timeMs = focusedEntity.getWorld().getTime()*50+(long)(storedTickDelta*50);
        Quaternionf gravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs).conjugate();

        double entityX = MathHelper.lerp((double) tickDelta, focusedEntity.prevX, focusedEntity.getX());
        double entityY = MathHelper.lerp((double) tickDelta, focusedEntity.prevY, focusedEntity.getY());
        double entityZ = MathHelper.lerp((double) tickDelta, focusedEntity.prevZ, focusedEntity.getZ());

        double currentCameraY = MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY);

        Vector3f eyeOffset = new Vector3f(0, (float) currentCameraY, 0);
        eyeOffset.rotate(gravityRotation);

        original.call(
                this,
                entityX + eyeOffset.x(),
                entityY + eyeOffset.y(),
                entityZ + eyeOffset.z()
        );
    }

    @Inject(
            method = "setRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
                    shift = At.Shift.AFTER
            )
    )
    private void inject_setRotation(CallbackInfo ci) {
        if(this.focusedEntity !=null) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.focusedEntity);
            Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(focusedEntity);
            if(animationOptional.isEmpty()) return;
            RotationAnimation animation = animationOptional.get();
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) return;
            long timeMs = focusedEntity.getWorld().getTime()*50+(long)(storedTickDelta*50);
            Quaternionf rotation = animation.getCurrentGravityRotation(gravityDirection, timeMs).conjugate();
            Quaternionf product = CompatMath.hamiltonProduct(rotation,this.rotation);
            this.rotation.set(product.x(), product.y(), product.z(), product.w());
        }
    }
}
