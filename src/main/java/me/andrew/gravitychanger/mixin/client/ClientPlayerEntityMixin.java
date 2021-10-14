package me.andrew.gravitychanger.mixin.client;

import com.mojang.authlib.GameProfile;
import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow protected abstract boolean wouldCollideAt(BlockPos pos);

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_tickMovement_getVelocity_0(ClientPlayerEntity clientPlayerEntity) {
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) clientPlayerEntity;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        return RotationUtil.vecWorldToPlayer(clientPlayerEntity.getVelocity(), gravityDirection);
    }

    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            )
    )
    private void redirect_tickMovement_setVelocity_0(ClientPlayerEntity clientPlayerEntity, Vec3d vec3d) {
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) clientPlayerEntity;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        clientPlayerEntity.setVelocity(RotationUtil.vecPlayerToWorld(vec3d, gravityDirection));
    }

    @Redirect(
            method = "wouldCollideAt",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/util/math/Box",
                    ordinal = 0
            )
    )
    private Box redirect_wouldCollideAt_new_0(double x1, double y1, double z1, double x2, double y2, double z2, BlockPos pos) {
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) this;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

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
        ci.cancel();

        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) this;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        Vec3d pos = RotationUtil.vecPlayerToWorld(x - this.getX(), 0.0D, z - this.getZ(), gravityDirection).add(this.getPos());
        BlockPos blockPos = new BlockPos(pos);
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
                    direction = worldDirection;
                }
            }

            if (direction != null) {
                Vec3d velocity = this.getVelocity();
                if (direction.getAxis() == Direction.Axis.X) {
                    this.setVelocity(0.1D * (double)direction.getOffsetX(), velocity.y, velocity.z);
                } else if(direction.getAxis() == Direction.Axis.Z) {
                    this.setVelocity(velocity.x, velocity.y, 0.1D * (double)direction.getOffsetZ());
                } else {
                    this.setVelocity(velocity.x, 0.1D * (double)direction.getOffsetY(), velocity.z);
                }
            }

        }
    }
}
