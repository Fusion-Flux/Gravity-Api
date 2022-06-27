package com.fusionflux.gravity_api.mixin.client;

import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.QuaternionUtil;
import com.fusionflux.gravity_api.util.RotationUtil;

import com.fusionflux.gravity_api.util.EntityTags;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow @Final private static RenderLayer SHADOW_LAYER;

    @Shadow private boolean renderShadows;

    @Shadow private static void drawShadowVertex(MatrixStack.Entry entry, VertexConsumer vertices, float alpha, float x, float y, float z, float u, float v) {}

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_render_0(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(!(entity instanceof ProjectileEntity) && !(entity instanceof ExperienceOrbEntity) && !entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITY_RENDERING)) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            if (!this.renderShadows) return;

            matrices.push();
            if(entity instanceof ClientPlayerEntity) {
                matrices.multiply(QuaternionUtil.inversed(RotationAnimation.getCurrentGravityRotation(gravityDirection)));
            }else{
                matrices.multiply(RotationUtil.getCameraRotationQuaternion(gravityDirection));
            }
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
                    ordinal = 1
            )
    )
    private void inject_render_1(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(!(entity instanceof ProjectileEntity) && !(entity instanceof ExperienceOrbEntity) && !entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITY_RENDERING)) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            if (!this.renderShadows) return;

            matrices.pop();
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_render_2(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(!(entity instanceof ProjectileEntity) && !(entity instanceof ExperienceOrbEntity) && !entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITY_RENDERING)) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            if (gravityDirection == Direction.DOWN) return;
            if (!this.renderShadows) return;

            matrices.multiply(RotationUtil.getCameraRotationQuaternion(gravityDirection));
        }
    }

    @Inject(
            method = "renderShadow",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void inject_renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) return;

        ci.cancel();

        double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
        Vec3d minShadowPos = RotationUtil.vecPlayerToWorld((double) -radius, (double) -radius, (double) -radius, gravityDirection).add(x, y, z);
        Vec3d maxShadowPos = RotationUtil.vecPlayerToWorld((double) radius, 0.0D, (double) radius, gravityDirection).add(x, y, z);
        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SHADOW_LAYER);

        for(BlockPos blockPos : BlockPos.iterate(new BlockPos(minShadowPos), new BlockPos(maxShadowPos))) {
            gravitychanger$renderShadowPartPlayer(entry, vertexConsumer, world, blockPos, x, y, z, radius, opacity, gravityDirection);
        }
    }

    private static void gravitychanger$renderShadowPartPlayer(MatrixStack.Entry entry, VertexConsumer vertices, WorldView world, BlockPos pos, double x, double y, double z, float radius, float opacity, Direction gravityDirection) {
        BlockPos posBelow = pos.offset(gravityDirection);
        BlockState blockStateBelow = world.getBlockState(posBelow);
        if (blockStateBelow.getRenderType() != BlockRenderType.INVISIBLE && world.getLightLevel(pos) > 3) {
            if (blockStateBelow.isFullCube(world, posBelow)) {
                VoxelShape voxelShape = blockStateBelow.getOutlineShape(world, posBelow);
                if (!voxelShape.isEmpty()) {
                    Vec3d playerPos = RotationUtil.vecWorldToPlayer(x, y, z, gravityDirection);
                    float alpha = (float)(((double)opacity - (playerPos.y - (RotationUtil.vecWorldToPlayer(Vec3d.ofCenter(pos), gravityDirection).y - 0.5D)) / 2.0D) * 0.5D * (double)world.getBrightness(pos));
                    if (alpha >= 0.0F) {
                        if (alpha > 1.0F) {
                            alpha = 1.0F;
                        }

                        Vec3d centerPos = Vec3d.ofCenter(pos);
                        Vec3d playerCenterPos = RotationUtil.vecWorldToPlayer(centerPos, gravityDirection);

                        Vec3d playerRelNN = playerCenterPos.add(-0.5D, -0.5D, -0.5D).subtract(playerPos);
                        Vec3d playerRelPP = playerCenterPos.add( 0.5D, -0.5D,  0.5D).subtract(playerPos);

                        Vec3d relNN = RotationUtil.vecWorldToPlayer(centerPos.add(RotationUtil.vecPlayerToWorld(-0.5D, -0.5D, -0.5D, gravityDirection)).subtract(x, y, z), gravityDirection);
                        Vec3d relNP = RotationUtil.vecWorldToPlayer(centerPos.add(RotationUtil.vecPlayerToWorld(-0.5D, -0.5D,  0.5D, gravityDirection)).subtract(x, y, z), gravityDirection);
                        Vec3d relPN = RotationUtil.vecWorldToPlayer(centerPos.add(RotationUtil.vecPlayerToWorld( 0.5D, -0.5D, -0.5D, gravityDirection)).subtract(x, y, z), gravityDirection);
                        Vec3d relPP = RotationUtil.vecWorldToPlayer(centerPos.add(RotationUtil.vecPlayerToWorld(0.5D, -0.5D,  0.5D, gravityDirection)).subtract(x, y, z), gravityDirection);

                        float minU = -(float) playerRelNN.x / 2.0F / radius + 0.5F;
                        float maxU = -(float) playerRelPP.x / 2.0F / radius + 0.5F;
                        float minV = -(float) playerRelNN.z / 2.0F / radius + 0.5F;
                        float maxV = -(float) playerRelPP.z / 2.0F / radius + 0.5F;

                        drawShadowVertex(entry, vertices, alpha, (float) relNN.x, (float) relNN.y, (float) relNN.z, minU, minV);
                        drawShadowVertex(entry, vertices, alpha, (float) relNP.x, (float) relNP.y, (float) relNP.z, minU, maxV);
                        drawShadowVertex(entry, vertices, alpha, (float) relPP.x, (float) relPP.y, (float) relPP.z, maxU, maxV);
                        drawShadowVertex(entry, vertices, alpha, (float) relPN.x, (float) relPN.y, (float) relPN.z, maxU, minV);
                    }
                }
            }
        }
    }

    @ModifyVariable(
            method = "renderHitbox",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/math/Box;offset(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private static Box modify_renderHitbox_Box_0(Box box, MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return box;
        }

        return RotationUtil.boxWorldToPlayer(box, gravityDirection);
    }

    @ModifyVariable(
            method = "renderHitbox",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private static Vec3d modify_renderHitbox_Vec3d_0(Vec3d vec3d, MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
}
