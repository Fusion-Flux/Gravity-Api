package com.fusionflux.gravity_api.mixin.client;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.CompatMath;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin {
    //@Redirect(
    //        method = "renderLabelIfPresent",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getRotation()Lnet/minecraft/util/math/Quaternion;",
    //                ordinal = 0
    //        )
    //)
    //private Quaternion redirect_renderLabelIfPresent_getRotation_0(EntityRenderDispatcher entityRenderDispatcher, Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    //    Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
    //    if(gravityDirection == Direction.DOWN) {
    //        return entityRenderDispatcher.getRotation();
    //    }
////
    //    Quaternion quaternion = RotationUtil.getCameraRotationQuaternion(gravityDirection).copy();
    //    quaternion.conjugate();
    //    quaternion.hamiltonProduct(entityRenderDispatcher.getRotation().copy());
    //    return quaternion;
    //}


    @ModifyExpressionValue(
            method = "renderLabelIfPresent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getRotation()Lorg/joml/Quaternionf;",
                    ordinal = 0
            )
    )
    private Quaternionf modifyExpressionValue_renderLabelIfPresent_getRotation_0(Quaternionf originalRotation, Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return originalRotation;
        }
        Quaternionf quaternion = RotationUtil.getCameraRotationQuaternion(gravityDirection).conjugate();
        return CompatMath.hamiltonProduct(quaternion,originalRotation);
    }
}
