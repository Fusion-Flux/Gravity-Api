package com.fusionflux.gravity_api.mixin.client;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow protected abstract boolean wouldCollideAt(BlockPos pos);

    @Redirect(
            method = "wouldCollideAt",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/util/math/Box",
                    ordinal = 0
            )
    )
    private Box redirect_wouldCollideAt_new_0(double x1, double y1, double z1, double x2, double y2, double z2, BlockPos pos) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this);
        if(gravityDirection == Direction.DOWN) {
            return new Box(x1, y1, z1, x2, y2, z2);
        }

        Box playerBox = this.getBoundingBox();
        Vec3d playerMask = RotationUtil.maskPlayerToWorld(0.0D, 1.0D, 0.0D, gravityDirection);
        Box posBox = new Box(pos);
        Vec3d posMask = RotationUtil.maskPlayerToWorld(1.0D, 0.0D, 1.0D, gravityDirection);

        return new Box(
                playerMask.multiply(playerBox.minX, playerBox.minY, playerBox.minZ).add(posMask.multiply(posBox.minX, posBox.minY, posBox.minZ)),
                playerMask.multiply(playerBox.maxX, playerBox.maxY, playerBox.maxZ).add(posMask.multiply(posBox.maxX, posBox.maxY, posBox.maxZ))
        );
    }

    @Inject(
            method = "pushOutOfBlocks",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_pushOutOfBlocks(double x, double z, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this);
        if(gravityDirection == Direction.DOWN) return;

        ci.cancel();

        Vec3d pos = RotationUtil.vecPlayerToWorld(x - this.getX(), 0.0D, z - this.getZ(), gravityDirection).add(this.getPos());
        BlockPos blockPos = new BlockPos((int) pos.x,(int) pos.y,(int) pos.z);
        if (this.wouldCollideAt(blockPos)) {
            double dx = pos.x - (double)blockPos.getX();
            double dy = pos.y - (double)blockPos.getY();
            double dz = pos.z - (double)blockPos.getZ();
            Direction direction = null;
            double minDistToEdge = Double.MAX_VALUE;

            Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
            for(Direction playerDirection : directions) {
                Direction worldDirection = RotationUtil.dirPlayerToWorld(playerDirection, gravityDirection);

                double g = worldDirection.getAxis().choose(dx, dy, dz);
                double distToEdge = worldDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - g : g;
                if (distToEdge < minDistToEdge && !this.wouldCollideAt(blockPos.offset(worldDirection))) {
                    minDistToEdge = distToEdge;
                    direction = playerDirection;
                }
            }

            if (direction != null) {
                Vec3d velocity = this.getVelocity();
                if (direction.getAxis() == Direction.Axis.X) {
                    this.setVelocity(0.1D * (double)direction.getOffsetX(), velocity.y, velocity.z);
                } else if(direction.getAxis() == Direction.Axis.Z) {
                    this.setVelocity(velocity.x, velocity.y, 0.1D * (double)direction.getOffsetZ());
                }
            }
        }
    }
}
