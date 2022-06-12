package com.fusionflux.fusions_gravity_api.mixin.client;

import com.fusionflux.fusions_gravity_api.accessor.EntityAccessor;
import com.fusionflux.fusions_gravity_api.util.RotationUtil;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(double x, double y, double z);

    @Shadow private Entity focusedEntity;

    @Shadow @Final private Quaternion rotation;

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V",
                    ordinal = 0
            )
    )
    private void redirect_update_setPos_0(Camera camera, double x, double y, double z, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        Direction gravityDirection = ((EntityAccessor) focusedEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            this.setPos(x, y, z);
            return;
        }

        double entityLerpedY = MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.getY());

        Vec3d eyeOffset = RotationUtil.vecPlayerToWorld(0, y - entityLerpedY, 0, gravityDirection);

        this.setPos(
                x + eyeOffset.x,
                entityLerpedY + eyeOffset.y,
                z + eyeOffset.z
        );
    }

    @Inject(
            method = "setRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Quaternion;hamiltonProduct(Lnet/minecraft/util/math/Quaternion;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_setRotation(CallbackInfo ci) {
        if(this.focusedEntity !=null) {
            Direction gravityDirection = ((EntityAccessor) this.focusedEntity).gravitychanger$getAppliedGravityDirection();
            if (gravityDirection == Direction.DOWN) return;

            Quaternion rotation = RotationUtil.getCameraRotationQuaternion(gravityDirection).copy();
            rotation.hamiltonProduct(this.rotation);
            this.rotation.set(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW());
        }
    }
}
