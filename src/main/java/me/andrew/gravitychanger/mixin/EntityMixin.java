package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.GravityChangerMod;
import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {
    @Shadow private Vec3d pos;

    @Shadow private EntityDimensions dimensions;

    @Shadow private float standingEyeHeight;

    @Shadow public double prevX;

    @Shadow public double prevY;

    @Shadow public double prevZ;

    @Shadow public abstract double getX();

    @Shadow public abstract Vec3d getEyePos();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow public World world;

    @Shadow public abstract int getBlockX();

    @Shadow public abstract int getBlockZ();

    @Shadow protected boolean submergedInWater;

    @Shadow public boolean noClip;

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract boolean hasPassengers();

    @Shadow public abstract Box getBoundingBox();

    @Shadow public static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) { return null; };

    @Shadow public abstract Vec3d getPos();

    @Shadow @Final protected Random random;

    @Shadow public abstract boolean isConnectedThroughVehicle(Entity entity);

    @Shadow public abstract void addVelocity(double deltaX, double deltaY, double deltaZ);

    @Shadow protected abstract void tickInVoid();
    
    @Shadow public abstract boolean isSubmergedIn(TagKey<Fluid> fluidTag);
    
    @Shadow @Final private Set<TagKey<Fluid>> submergedFluidTag;
    
    @Shadow public abstract double getEyeY();
    
    @Shadow @Nullable public abstract Entity getVehicle();

    @Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        return Direction.DOWN;
    }

    @Inject(
            method = "onTrackedDataSet",
            at = @At("RETURN")
    )
    private void inject_onTrackedDataSet(TrackedData<?> data, CallbackInfo ci) {
        if(this instanceof RotatableEntityAccessor rotatableEntityAccessor) {
            rotatableEntityAccessor.gravitychanger$onTrackedData(data);
        }
    }

    @Inject(
            method = "calculateBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_calculateBoundingBox(CallbackInfoReturnable<Box> cir) {
        Entity entity = ((Entity)(Object)this);
        if(entity instanceof ProjectileEntity) return;

        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Box box = cir.getReturnValue().offset(this.pos.negate());
        if(gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.offset(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection).offset(this.pos));
    }

    @Inject(
            method = "calculateBoundsForPose",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_calculateBoundsForPose(EntityPose pos, CallbackInfoReturnable<Box> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Box box = cir.getReturnValue().offset(this.pos.negate());
        if(gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.offset(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection).offset(this.pos));
    }

    @Inject(
            method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_getRotationVector(CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(cir.getReturnValue(), gravityDirection));
    }

    @Inject(
            method = "getVelocityAffectingPos",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(new BlockPos(this.pos.add(Vec3d.of(gravityDirection.getVector()).multiply(0.5000001D))));
    }

    @Inject(
            method = "getEyePos",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getEyePos(CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(0.0D, this.standingEyeHeight, 0.0D, gravityDirection).add(this.pos));
    }

    @Inject(
            method = "getCameraPosVec",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Vec3d vec3d = RotationUtil.vecPlayerToWorld(0.0D, this.standingEyeHeight, 0.0D, gravityDirection);

        double d = MathHelper.lerp((double)tickDelta, this.prevX, this.getX()) + vec3d.x;
        double e = MathHelper.lerp((double)tickDelta, this.prevY, this.getY()) + vec3d.y;
        double f = MathHelper.lerp((double)tickDelta, this.prevZ, this.getZ()) + vec3d.z;
        cir.setReturnValue(new Vec3d(d, e, f));
    }

    @Inject(
            method = "getBrightnessAtEyes",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getBrightnessAtEyes(CallbackInfoReturnable<Float> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(this.world.isPosLoaded(this.getBlockX(), this.getBlockZ()) ? this.world.getBrightness(new BlockPos(this.getEyePos())) : 0.0F);
    }

    @ModifyVariable(
            method = "move",
            at = @At("HEAD"),
            ordinal = 0
    )
    private Vec3d modify_move_Vec3d_0_0(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }


    @ModifyArg(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;multiply(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            index = 0
    )
    private Vec3d modify_move_multiply_0(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.maskPlayerToWorld(vec3d, gravityDirection);
    }

    @ModifyVariable(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private Vec3d modify_move_Vec3d_0_1(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    @ModifyVariable(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
                    ordinal = 0
            ),
            ordinal = 1
    )
    private Vec3d modify_move_Vec3d_1(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    @Inject(
            method = "getLandingPos",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getLandingPos(CallbackInfoReturnable<BlockPos> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        BlockPos blockPos = new BlockPos(RotationUtil.vecPlayerToWorld(0.0D, -0.20000000298023224D, 0.0D, gravityDirection).add(this.pos));
        // Probably not needed since these blocks only extend up
//        if (this.world.getBlockState(blockPos).isAir()) {
//            BlockPos blockPos2 = blockPos.offset(gravityDirection);
//            BlockState blockState = this.world.getBlockState(blockPos2);
//            if (blockState.isIn(BlockTags.FENCES) || blockState.isIn(BlockTags.WALLS) || blockState.getBlock() instanceof FenceGateBlock) {
//                cir.setReturnValue(blockPos2);
//                return;
//            }
//        }

        cir.setReturnValue(blockPos);
    }

    @ModifyVariable(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/World;getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private Vec3d modify_adjustMovementForCollisions_Vec3d_0(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    @Inject(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_adjustMovementForCollisions(CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(cir.getReturnValue(), gravityDirection));
    }

    @ModifyArgs(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"
            )
    )
    private void redirect_adjustMovementForCollisions_stretch_0(Args args) {
        Vec3d rotate = new Vec3d(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate,((EntityAccessor) this).gravitychanger$getAppliedGravityDirection());
        args.set(0,rotate.x);
        args.set(1,rotate.y);
        args.set(2,rotate.z);
    }

    @ModifyArgs(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;offset(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            )
    )
    private void redirect_adjustMovementForCollisions_offset_0(Args args) {
        Vec3d rotate = args.get(0);
        rotate = RotationUtil.vecPlayerToWorld(rotate,((EntityAccessor) this).gravitychanger$getAppliedGravityDirection());
        args.set(0,rotate);
    }

    @ModifyArgs(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;offset(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Box;",
                    ordinal = 1
            )
    )
    private void redirect_adjustMovementForCollisions_offset_1(Args args) {
        Vec3d rotate = args.get(0);
        rotate = RotationUtil.vecPlayerToWorld(rotate,((EntityAccessor) this).gravitychanger$getAppliedGravityDirection());
        args.set(0,rotate);
    }

    @ModifyVariable(
            method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("HEAD"),
            ordinal = 0
    )
    private static Vec3d modify_adjustMovementForCollisions_Vec3d_0(Vec3d vec3d, Entity entity) {
        if(entity == null) {
            return vec3d;
        }

        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    @Inject(
            method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void inject_adjustMovementForCollisions(Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3d> cir) {
        if(entity == null) return;

        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(RotationUtil.vecWorldToPlayer(cir.getReturnValue(), gravityDirection));
    }

    @Redirect(
            method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private static Vec3d redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions, Entity entity) {
        Direction gravityDirection;
        if(entity == null || (gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection()) == Direction.DOWN) {
            return adjustMovementForCollisions(movement, entityBoundingBox, collisions);
        }

        Vec3d playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);
        double playerMovementX = playerMovement.x;
        double playerMovementY = playerMovement.y;
        double playerMovementZ = playerMovement.z;
        Direction directionX = RotationUtil.dirPlayerToWorld(Direction.EAST, gravityDirection);
        Direction directionY = RotationUtil.dirPlayerToWorld(Direction.UP, gravityDirection);
        Direction directionZ = RotationUtil.dirPlayerToWorld(Direction.SOUTH, gravityDirection);
        if (playerMovementY != 0.0D) {
            playerMovementY = VoxelShapes.calculateMaxOffset(directionY.getAxis(), entityBoundingBox, collisions, playerMovementY * directionY.getDirection().offset()) * directionY.getDirection().offset();
            if (playerMovementY != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(0.0D, playerMovementY, 0.0D, gravityDirection));
            }
        }

        boolean isZLargerThanX = Math.abs(playerMovementX) < Math.abs(playerMovementZ);
        if (isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getDirection().offset()) * directionZ.getDirection().offset();
            if (playerMovementZ != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(0.0D, 0.0D, playerMovementZ, gravityDirection));
            }
        }

        if (playerMovementX != 0.0D) {
            playerMovementX = VoxelShapes.calculateMaxOffset(directionX.getAxis(), entityBoundingBox, collisions, playerMovementX * directionX.getDirection().offset()) * directionX.getDirection().offset();
            if (!isZLargerThanX && playerMovementX != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(playerMovementX, 0.0D, 0.0D, gravityDirection));
            }
        }

        if (!isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getDirection().offset()) * directionZ.getDirection().offset();
        }

        return RotationUtil.vecPlayerToWorld(playerMovementX, playerMovementY, playerMovementZ, gravityDirection);
    }

    @ModifyArgs(
            method = "isInsideWall",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;of(Lnet/minecraft/util/math/Vec3d;DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            )
    )
    private void modify_isInsideWall_of_0(Args args) {
        Vec3d rotate = new Vec3d(args.get(1), args.get(2), args.get(3));
        rotate = RotationUtil.vecPlayerToWorld(rotate,((EntityAccessor) this).gravitychanger$getAppliedGravityDirection());
        args.set(1,rotate.x);
        args.set(2,rotate.y);
        args.set(3,rotate.z);
    }

    @Redirect(
            method = "getHorizontalFacing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw()F",
                    ordinal = 0
            )
    )
    private float redirect_getHorizontalFacing_getYaw_0(Entity entity) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getYaw();
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(), entity.getPitch(), gravityDirection).x;
    }

    @Inject(
            method = "spawnSprintingParticles",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_spawnSprintingParticles(CallbackInfo ci) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        ci.cancel();

        Vec3d floorPos = this.getPos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.20000000298023224D, 0.0D, gravityDirection));

        BlockPos blockPos = new BlockPos(floorPos);
        BlockState blockState = this.world.getBlockState(blockPos);
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            Vec3d particlePos = this.getPos().add(RotationUtil.vecPlayerToWorld((this.random.nextDouble() - 0.5D) * (double)this.dimensions.width, 0.1D, (this.random.nextDouble() - 0.5D) * (double)this.dimensions.width, gravityDirection));
            Vec3d playerVelocity = this.getVelocity();
            Vec3d particleVelocity = RotationUtil.vecPlayerToWorld(playerVelocity.x * -4.0D, 1.5D, playerVelocity.z * -4.0D, gravityDirection);
            this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), particlePos.x, particlePos.y, particlePos.z, particleVelocity.x, particleVelocity.y, particleVelocity.z);
        }
    }

    @ModifyVariable(
            method = "updateMovementInFluid",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 1
    )
    private Vec3d modify_updateMovementInFluid_Vec3d_0(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    @ModifyArg(
            method = "updateMovementInFluid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 1
            ),
            index = 0
    )
    private Vec3d modify_updateMovementInFluid_add_0(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }



    @Inject(
            method = "pushAwayFrom",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_pushAwayFrom(Entity entity, CallbackInfo ci) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        Direction otherGravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();

        if(gravityDirection == Direction.DOWN && otherGravityDirection == Direction.DOWN) return;

        ci.cancel();

        if (!this.isConnectedThroughVehicle(entity)) {
            if (!entity.noClip && !this.noClip) {
                Vec3d entityOffset = entity.getBoundingBox().getCenter().subtract(this.getBoundingBox().getCenter());

                {
                    Vec3d playerEntityOffset = RotationUtil.vecWorldToPlayer(entityOffset, gravityDirection);
                    double dx = playerEntityOffset.x;
                    double dz = playerEntityOffset.z;
                    double f = MathHelper.absMax(dx, dz);
                    if(f >= 0.009999999776482582D) {
                        f = Math.sqrt(f);
                        dx /= f;
                        dz /= f;
                        double g = 1.0D / f;
                        if(g > 1.0D) {
                            g = 1.0D;
                        }

                        dx *= g;
                        dz *= g;
                        dx *= 0.05000000074505806D;
                        dz *= 0.05000000074505806D;
                        if(!this.hasPassengers()) {
                            this.addVelocity(-dx, 0.0D, -dz);
                        }
                    }
                }

                {
                    Vec3d entityEntityOffset = RotationUtil.vecWorldToPlayer(entityOffset, otherGravityDirection);
                    double dx = entityEntityOffset.x;
                    double dz = entityEntityOffset.z;
                    double f = MathHelper.absMax(dx, dz);
                    if(f >= 0.009999999776482582D) {
                        f = Math.sqrt(f);
                        dx /= f;
                        dz /= f;
                        double g = 1.0D / f;
                        if(g > 1.0D) {
                            g = 1.0D;
                        }

                        dx *= g;
                        dz *= g;
                        dx *= 0.05000000074505806D;
                        dz *= 0.05000000074505806D;
                        if(!entity.hasPassengers()) {
                            entity.addVelocity(dx, 0.0D, dz);
                        }
                    }
                }
            }
        }
    }

    @Inject(
            method = "attemptTickInVoid",
            at = @At("HEAD")
    )
    private void inject_attemptTickInVoid(CallbackInfo ci) {
        if (GravityChangerMod.config.voidDamageAboveWorld && this.getY() > (double)(this.world.getTopY() + 256)) {
            this.tickInVoid();
        }
    }

    @ModifyArgs(
            method = "doesNotCollide(DDD)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;offset(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            )
    )
    private void redirect_doesNotCollide_offset_0(Args args) {
        Vec3d rotate = new Vec3d(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate,((EntityAccessor) this).gravitychanger$getAppliedGravityDirection());
        args.set(0,rotate.x);
        args.set(1,rotate.y);
        args.set(2,rotate.z);
    }


    @ModifyVariable(
            method = "updateSubmergedInWaterState",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private double submergedInWaterEyeFix(double d) {
        d = this.getEyePos().getY();
        return d;
    }

    @ModifyVariable(
            method = "updateSubmergedInWaterState",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private BlockPos submergedInWaterPosFix(BlockPos blockpos) {
        blockpos = new BlockPos(this.getEyePos());
        return blockpos;
    }

}
