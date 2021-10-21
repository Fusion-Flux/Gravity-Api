package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.Stream;

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

    @Shadow public abstract boolean isSubmergedIn(Tag<Fluid> fluidTag);

    @Shadow @Nullable protected Tag<Fluid> submergedFluidTag;

    @Shadow private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) { return  null; };

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow public boolean noClip;

    @Shadow public abstract void setPosition(double x, double y, double z);

    @Shadow public boolean wasOnFire;

    @Shadow public abstract boolean isOnFire();

    @Shadow protected abstract Vec3d adjustMovementForPiston(Vec3d movement);

    @Shadow protected Vec3d movementMultiplier;

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow protected abstract Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type);

    @Shadow protected abstract Vec3d adjustMovementForCollisions(Vec3d movement);

    @Shadow public boolean horizontalCollision;

    @Shadow public boolean verticalCollision;

    @Shadow protected boolean onGround;

    @Shadow public abstract BlockPos getLandingPos();

    @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition);

    @Shadow public abstract boolean isRemoved();

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract boolean bypassesSteppingEffects();

    @Shadow protected abstract Entity.MoveEffect getMoveEffect();

    @Shadow public abstract boolean hasVehicle();

    @Shadow public float field_28627;

    @Shadow public float horizontalSpeed;

    @Shadow public float distanceTraveled;

    @Shadow private float nextStepSoundDistance;

    @Shadow protected abstract float calculateNextStepSoundDistance();

    @Shadow public abstract boolean isTouchingWater();

    @Shadow public abstract boolean hasPassengers();

    @Shadow @Nullable public abstract Entity getPrimaryPassenger();

    @Shadow protected abstract void playSwimSound(float volume);

    @Shadow public abstract void emitGameEvent(GameEvent event);

    @Shadow protected abstract void playAmethystChimeSound(BlockState state);

    @Shadow protected abstract void playStepSound(BlockPos pos, BlockState state);

    @Shadow protected abstract void addAirTravelEffects();

    @Shadow protected abstract void tryCheckBlockCollision();

    @Shadow protected abstract float getVelocityMultiplier();

    @Shadow public abstract Box getBoundingBox();

    @Shadow private int fireTicks;

    @Shadow public abstract void setFireTicks(int ticks);

    @Shadow protected abstract int getBurningDuration();

    @Shadow public boolean inPowderSnow;

    @Shadow public abstract boolean isWet();

    @Shadow protected abstract void playExtinguishSound();

    @Shadow public float stepHeight;

    @Shadow public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, ShapeContext context, ReusableStream<VoxelShape> collisions) { return null; };

    @Shadow public static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, ReusableStream<VoxelShape> collisions) { return null; };

    @Shadow public abstract Vec3d getPos();

    @Shadow @Final protected Random random;

    @Shadow public abstract void setVelocity(double x, double y, double z);

    @Shadow public abstract boolean isConnectedThroughVehicle(Entity entity);

    @Shadow public abstract void addVelocity(double deltaX, double deltaY, double deltaZ);

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

    @Inject(
            method = "move",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_move(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        ci.cancel();

        if (this.noClip) {
            this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        } else {
            this.wasOnFire = this.isOnFire();
            if (movementType == MovementType.PISTON) {
                movement = this.adjustMovementForPiston(movement);
                if (movement.equals(Vec3d.ZERO)) {
                    return;
                }
            }

            this.world.getProfiler().push("move");
            if (this.movementMultiplier.lengthSquared() > 1.0E-7D) {
                movement = movement.multiply(RotationUtil.maskPlayerToWorld(this.movementMultiplier, gravityDirection));
                this.movementMultiplier = Vec3d.ZERO;
                this.setVelocity(Vec3d.ZERO);
            }

            movement = this.adjustMovementForSneaking(movement, movementType);
            Vec3d adjustedMovement = this.adjustMovementForCollisions(movement);
            if (adjustedMovement.lengthSquared() > 1.0E-7D) {
                this.setPosition(this.getX() + adjustedMovement.x, this.getY() + adjustedMovement.y, this.getZ() + adjustedMovement.z);
            }

            this.world.getProfiler().pop();
            this.world.getProfiler().push("rest");
            Vec3d playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);
            Vec3d playerAdjustedMovement = RotationUtil.vecWorldToPlayer(adjustedMovement, gravityDirection);
            this.horizontalCollision = !MathHelper.approximatelyEquals(playerMovement.x, playerAdjustedMovement.x) || !MathHelper.approximatelyEquals(playerMovement.z, playerAdjustedMovement.z);
            this.verticalCollision = playerMovement.y != playerAdjustedMovement.y;
            this.onGround = this.verticalCollision && playerMovement.y < 0.0D;
            BlockPos blockPos = this.getLandingPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            this.fall(playerAdjustedMovement.y, this.onGround, blockState, blockPos);
            if (this.isRemoved()) {
                this.world.getProfiler().pop();
            } else {
                Vec3d playerVelocity = this.getVelocity();
                if (playerMovement.x != playerAdjustedMovement.x) {
                    this.setVelocity(0.0D, playerVelocity.y, playerVelocity.z);
                }

                if (playerMovement.z != playerAdjustedMovement.z) {
                    this.setVelocity(playerVelocity.x, playerVelocity.y, 0.0D);
                }

                Block block = blockState.getBlock();
                if (playerMovement.y != playerAdjustedMovement.y) {
                    block.onEntityLand(this.world, (Entity)(Object) this);
                }

                if (this.onGround && !this.bypassesSteppingEffects()) {
                    block.onSteppedOn(this.world, blockPos, blockState, (Entity)(Object) this);
                }

                Entity.MoveEffect moveEffect = this.getMoveEffect();
                if (moveEffect.hasAny() && !this.hasVehicle()) {
                    double playerAdjustedMovementX = playerAdjustedMovement.x;
                    double playerAdjustedMovementY = playerAdjustedMovement.y;
                    double playerAdjustedMovementZ = playerAdjustedMovement.z;
                    this.field_28627 = (float)((double)this.field_28627 + playerAdjustedMovement.length() * 0.6D);
                    if (!blockState.isIn(BlockTags.CLIMBABLE) && !blockState.isOf(Blocks.POWDER_SNOW)) {
                        playerAdjustedMovementY = 0.0D;
                    }

                    this.horizontalSpeed += (float) playerAdjustedMovement.horizontalLength() * 0.6F;
                    this.distanceTraveled += (float)Math.sqrt(playerAdjustedMovementX * playerAdjustedMovementX + playerAdjustedMovementY * playerAdjustedMovementY + playerAdjustedMovementZ * playerAdjustedMovementZ) * 0.6F;
                    if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
                        this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
                        if (this.isTouchingWater()) {
                            if (moveEffect.playsSounds()) {
                                Entity primaryPassenger = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : (Entity)(Object) this;
                                float volumeMultiplier = primaryPassenger == (Object) this ? 0.35F : 0.4F;
                                Vec3d primaryPassengerVelocity = primaryPassenger.getVelocity();
                                float volume = Math.min(1.0F, (float)Math.sqrt(primaryPassengerVelocity.x * primaryPassengerVelocity.x * 0.20000000298023224D + primaryPassengerVelocity.y * primaryPassengerVelocity.y + primaryPassengerVelocity.z * primaryPassengerVelocity.z * 0.20000000298023224D) * volumeMultiplier);
                                this.playSwimSound(volume);
                            }

                            if (moveEffect.emitsGameEvents()) {
                                this.emitGameEvent(GameEvent.SWIM);
                            }
                        } else {
                            if (moveEffect.playsSounds()) {
                                this.playAmethystChimeSound(blockState);
                                this.playStepSound(blockPos, blockState);
                            }

                            if (moveEffect.emitsGameEvents() && !blockState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
                                this.emitGameEvent(GameEvent.STEP);
                            }
                        }
                    } else if (blockState.isAir()) {
                        this.addAirTravelEffects();
                    }
                }

                this.tryCheckBlockCollision();
                float velocityMultiplier = this.getVelocityMultiplier();
                this.setVelocity(this.getVelocity().multiply(velocityMultiplier, 1.0D, velocityMultiplier));
                if (this.world.getStatesInBoxIfLoaded(this.getBoundingBox().contract(1.0E-6D)).noneMatch((state) -> {
                    return state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA);
                })) {
                    if (this.fireTicks <= 0) {
                        this.setFireTicks(-this.getBurningDuration());
                    }

                    if (this.wasOnFire && (this.inPowderSnow || this.isWet())) {
                        this.playExtinguishSound();
                    }
                }

                if (this.isOnFire() && (this.inPowderSnow || this.isWet())) {
                    this.setFireTicks(-this.getBurningDuration());
                }

                this.world.getProfiler().pop();
            }
        }
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

    @Inject(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void adjustMovementForCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Box box = this.getBoundingBox();
        ShapeContext shapeContext = ShapeContext.of((Entity)(Object) this);
        VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();
        Stream<VoxelShape> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7D)), BooleanBiFunction.AND) ? Stream.empty() : Stream.of(voxelShape);
        Stream<VoxelShape> stream2 = this.world.getEntityCollisions((Entity)(Object) this, box.stretch(movement), (entity) -> {
            return true;
        });
        ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(Stream.concat(stream2, stream));
        Vec3d playerAdjustedMovement = movement.lengthSquared() == 0.0D ? movement : RotationUtil.vecWorldToPlayer(adjustMovementForCollisions((Entity)(Object) this, movement, box, this.world, shapeContext, reusableStream), gravityDirection);
        Vec3d playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);
        boolean collidedX = playerMovement.x != playerAdjustedMovement.x;
        boolean collidedY = playerMovement.y != playerAdjustedMovement.y;
        boolean collidedZ = playerMovement.z != playerAdjustedMovement.z;
        boolean onGround = this.onGround || collidedY && playerMovement.y < 0.0D;
        if (this.stepHeight > 0.0F && onGround && (collidedX || collidedZ)) {
            Vec3d playerAdjustedStepMovement1 = RotationUtil.vecWorldToPlayer(adjustMovementForCollisions((Entity)(Object) this, RotationUtil.vecPlayerToWorld(playerMovement.x, (double)this.stepHeight, playerMovement.z, gravityDirection), box, this.world, shapeContext, reusableStream), gravityDirection);
            Vec3d playerAdjustedStepMovement2 = RotationUtil.vecWorldToPlayer(adjustMovementForCollisions((Entity)(Object) this, RotationUtil.vecPlayerToWorld(0.0D, (double)this.stepHeight, 0.0D, gravityDirection), box.stretch(RotationUtil.vecPlayerToWorld(playerMovement.x, 0.0D, playerMovement.z, gravityDirection)), this.world, shapeContext, reusableStream), gravityDirection);
            if (playerAdjustedStepMovement2.y < (double)this.stepHeight) {
                Vec3d playerAdjustedStepMovement3 = RotationUtil.vecWorldToPlayer(adjustMovementForCollisions((Entity)(Object) this, RotationUtil.vecPlayerToWorld(new Vec3d(playerMovement.x, 0.0D, playerMovement.z), gravityDirection), box.offset(RotationUtil.vecPlayerToWorld(playerAdjustedStepMovement2, gravityDirection)), this.world, shapeContext, reusableStream), gravityDirection).add(playerAdjustedStepMovement2);
                if (playerAdjustedStepMovement3.horizontalLengthSquared() > playerAdjustedStepMovement1.horizontalLengthSquared()) {
                    playerAdjustedStepMovement1 = playerAdjustedStepMovement3;
                }
            }

            if (playerAdjustedStepMovement1.horizontalLengthSquared() > playerAdjustedMovement.horizontalLengthSquared()) {
                cir.setReturnValue(RotationUtil.vecPlayerToWorld(playerAdjustedStepMovement1, gravityDirection).add(adjustMovementForCollisions((Entity)(Object) this, RotationUtil.vecPlayerToWorld(0.0D, -playerAdjustedStepMovement1.y + playerMovement.y, 0.0D, gravityDirection), box.offset(RotationUtil.vecPlayerToWorld(playerAdjustedStepMovement1, gravityDirection)), this.world, shapeContext, reusableStream)));
                return;
            }
        }

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(playerAdjustedMovement, gravityDirection));
    }

    @Redirect(
            method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Lnet/minecraft/block/ShapeContext;Lnet/minecraft/util/collection/ReusableStream;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/util/collection/ReusableStream;)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private static Vec3d redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(Vec3d movement, Box entityBoundingBox, ReusableStream<VoxelShape> collisions, @Nullable Entity entity, Vec3d movementIgnored, Box entityBoundingBoxIgnored, World worldIgnored, ShapeContext contextIgnored, ReusableStream<VoxelShape> collisionsIgnored) {
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
            playerMovementY = VoxelShapes.calculateMaxOffset(directionY.getAxis(), entityBoundingBox, collisions.stream(), playerMovementY * directionY.getDirection().offset()) * directionY.getDirection().offset();
            if (playerMovementY != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(0.0D, playerMovementY, 0.0D, gravityDirection));
            }
        }

        boolean isZLargerThanX = Math.abs(playerMovementX) < Math.abs(playerMovementZ);
        if (isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisions.stream(), playerMovementZ * directionZ.getDirection().offset()) * directionZ.getDirection().offset();
            if (playerMovementZ != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(0.0D, 0.0D, playerMovementZ, gravityDirection));
            }
        }

        if (playerMovementX != 0.0D) {
            playerMovementX = VoxelShapes.calculateMaxOffset(directionX.getAxis(), entityBoundingBox, collisions.stream(), playerMovementX * directionX.getDirection().offset()) * directionX.getDirection().offset();
            if (!isZLargerThanX && playerMovementX != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(playerMovementX, 0.0D, 0.0D, gravityDirection));
            }
        }

        if (!isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisions.stream(), playerMovementZ * directionZ.getDirection().offset()) * directionZ.getDirection().offset();
        }

        return RotationUtil.vecPlayerToWorld(playerMovementX, playerMovementY, playerMovementZ, gravityDirection);
    }

    @Inject(
            method = "updateSubmergedInWaterState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_updateSubmergedInWaterState(CallbackInfo ci) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        ci.cancel();

        this.submergedInWater = this.isSubmergedIn(FluidTags.WATER);
        this.submergedFluidTag = null;
        Vec3d mouthPos = this.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.1111111119389534D, 0.0D, gravityDirection));
        BlockPos blockPos = new BlockPos(mouthPos);
        FluidState fluidState = this.world.getFluidState(blockPos);
        Iterator<Tag<Fluid>> var6 = FluidTags.getTags().iterator();

        Tag<Fluid> tag;
        do {
            if (!var6.hasNext()) {
                return;
            }

            tag = var6.next();
        } while(!fluidState.isIn(tag));

        Box box = new Box(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + fluidState.getHeight(this.world, blockPos), blockPos.getZ() + 1);
        if (box.contains(mouthPos)) {
            this.submergedFluidTag = tag;
        }
    }

    @Redirect(
            method = "isInsideWall",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;of(Lnet/minecraft/util/math/Vec3d;DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            )
    )
    private Box redirect_isInsideWall_of_0(Vec3d center, double dx, double dy, double dz) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return Box.of(center, dx, dy, dz);
        }

        return RotationUtil.boxPlayerToWorld(Box.of(Vec3d.ZERO, dx, dy, dz), gravityDirection).offset(center);
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
}
