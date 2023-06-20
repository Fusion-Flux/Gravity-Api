package com.fusionflux.gravity_api.mixin.client;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
    @Inject(
            method = "getInWallBlockState",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void inject_getInWallBlockState(PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(player);
        if(gravityDirection == Direction.DOWN) return;

        cir.cancel();

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        Vec3d eyePos = player.getEyePos();
        Vector3f multipliers = RotationUtil.vecPlayerToWorld(player.getWidth() * 0.8F, 0.1F, player.getWidth() * 0.8F, gravityDirection);
        for(int i = 0; i < 8; ++i) {
            double d = eyePos.x + (double)(((float)((i >> 0) % 2) - 0.5F) * multipliers.x());
            double e = eyePos.y + (double)(((float)((i >> 1) % 2) - 0.5F) * multipliers.y());
            double f = eyePos.z + (double)(((float)((i >> 2) % 2) - 0.5F) * multipliers.z());
            mutable.set(d, e, f);
            BlockState blockState = player.getWorld().getBlockState(mutable);
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE && blockState.shouldBlockVision(player.getWorld(), mutable)) {
                cir.setReturnValue(blockState);
            }
        }

        cir.setReturnValue(null);
    }
}
