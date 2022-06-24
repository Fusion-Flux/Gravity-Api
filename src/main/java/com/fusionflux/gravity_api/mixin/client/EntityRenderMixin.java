package com.fusionflux.gravity_api.mixin.client;

import com.fusionflux.gravity_api.accessor.EntityAccessor;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin {
    @Redirect(
            method = "renderLabelIfPresent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getRotation()Lnet/minecraft/util/math/Quaternion;",
                    ordinal = 0
            )
    )
    private Quaternion redirect_renderLabelIfPresent_getRotation_0(EntityRenderDispatcher entityRenderDispatcher, Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entityRenderDispatcher.getRotation();
        }

        Quaternion quaternion = RotationUtil.getCameraRotationQuaternion(gravityDirection).copy();
        quaternion.conjugate();
        quaternion.hamiltonProduct(entityRenderDispatcher.getRotation().copy());
        return quaternion;
    }
}
