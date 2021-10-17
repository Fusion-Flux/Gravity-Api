package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract void readCustomDataFromNbt(NbtCompound nbt);

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow public abstract boolean canMoveVoluntarily();

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow protected abstract boolean shouldSwimInFluids();

    @Shadow public abstract boolean canWalkOnFluid(Fluid fluid);

    @Shadow protected abstract float getBaseMovementSpeedMultiplier();

    @Shadow public abstract float getMovementSpeed();

    @Shadow public abstract boolean isClimbing();

    @Shadow public abstract Vec3d method_26317(double d, boolean bl, Vec3d vec3d);

    @Shadow public abstract boolean isFallFlying();

    @Shadow protected abstract SoundEvent getFallSound(int distance);

    @Shadow public abstract Vec3d method_26318(Vec3d vec3d, float f);

    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow public abstract boolean hasNoDrag();

    @Shadow public abstract void updateLimbs(LivingEntity entity, boolean flutter);

    @Shadow public abstract float getYaw(float tickDelta);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "travel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_travel(Vec3d movementInput, CallbackInfo ci) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        ci.cancel();

        if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
            double gravity = 0.08D;
            boolean isMovingDown = this.getVelocity().y <= 0.0D;
            if (isMovingDown && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                gravity = 0.01D;
                this.fallDistance = 0.0F;
            }

            FluidState fluidState = this.world.getFluidState(this.getBlockPos());
            float movementSpeedMultiplier;
            double playerY;
            if (this.isTouchingWater() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState.getFluid())) {
                playerY = RotationUtil.vecWorldToPlayer(this.getPos(), gravityDirection).y;
                movementSpeedMultiplier = this.isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
                float movementSpeed = 0.02F;
                float depthStriderMultiplier = (float) EnchantmentHelper.getDepthStrider((LivingEntity)(Object) this);
                if (depthStriderMultiplier > 3.0F) {
                    depthStriderMultiplier = 3.0F;
                }

                if (!this.onGround) {
                    depthStriderMultiplier *= 0.5F;
                }

                if (depthStriderMultiplier > 0.0F) {
                    movementSpeedMultiplier += (0.54600006F - movementSpeedMultiplier) * depthStriderMultiplier / 3.0F;
                    movementSpeed += (this.getMovementSpeed() - movementSpeed) * depthStriderMultiplier / 3.0F;
                }

                if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                    movementSpeedMultiplier = 0.96F;
                }

                this.updateVelocity(movementSpeed, movementInput);
                this.move(MovementType.SELF, RotationUtil.vecPlayerToWorld(this.getVelocity(), gravityDirection));
                Vec3d playerVelocity = this.getVelocity();
                if (this.horizontalCollision && this.isClimbing()) {
                    playerVelocity = new Vec3d(playerVelocity.x, 0.2D, playerVelocity.z);
                }

                this.setVelocity(playerVelocity.multiply(movementSpeedMultiplier, 0.800000011920929D, movementSpeedMultiplier));
                Vec3d playerAdjustedVelocity = this.method_26317(gravity, isMovingDown, this.getVelocity());
                this.setVelocity(playerAdjustedVelocity);
                Vec3d boxOffset = RotationUtil.vecPlayerToWorld(playerAdjustedVelocity.add(0.0D, 0.6000000238418579D - RotationUtil.vecWorldToPlayer(this.getPos(), gravityDirection).y + playerY, 0.0D), gravityDirection);
                if (this.horizontalCollision && this.doesNotCollide(boxOffset.x, boxOffset.y, boxOffset.z)) {
                    this.setVelocity(playerAdjustedVelocity.x, 0.30000001192092896D, playerAdjustedVelocity.z);
                }
            } else if (this.isInLava() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState.getFluid())) {
                playerY = RotationUtil.vecWorldToPlayer(this.getPos(), gravityDirection).y;
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, RotationUtil.vecPlayerToWorld(this.getVelocity(), gravityDirection));
                Vec3d playerVelocity;
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
                    this.setVelocity(this.getVelocity().multiply(0.5D, 0.800000011920929D, 0.5D));
                    playerVelocity = this.method_26317(gravity, isMovingDown, this.getVelocity());
                    this.setVelocity(playerVelocity);
                } else {
                    this.setVelocity(this.getVelocity().multiply(0.5D));
                }

                if (!this.hasNoGravity()) {
                    this.setVelocity(this.getVelocity().add(0.0D, -gravity / 4.0D, 0.0D));
                }

                playerVelocity = this.getVelocity();
                Vec3d boxOffset = RotationUtil.vecPlayerToWorld(playerVelocity.add(0.0D, 0.6000000238418579D - RotationUtil.vecWorldToPlayer(this.getPos(), gravityDirection).y + playerY, 0.0D), gravityDirection);
                if (this.horizontalCollision && this.doesNotCollide(boxOffset.x, boxOffset.y, boxOffset.z)) {
                    this.setVelocity(playerVelocity.x, 0.30000001192092896D, playerVelocity.z);
                }
            } else if (this.isFallFlying()) {
                Vec3d playerVelocity = this.getVelocity();
                if (playerVelocity.y > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                Vec3d playerRotationVector = RotationUtil.vecWorldToPlayer(this.getRotationVector(), gravityDirection);
                float playerPitch = this.getPitch() * 0.017453292F;
                double playerHorizontalRotationLength = Math.sqrt(playerRotationVector.x * playerRotationVector.x + playerRotationVector.z * playerRotationVector.z);
                double playerHorizontalVelocityLength = playerVelocity.horizontalLength();
                double playerRotationLength = playerRotationVector.length();
                float playerCosPitch = MathHelper.cos(playerPitch);
                playerCosPitch = (float)((double) playerCosPitch * (double) playerCosPitch * Math.min(1.0D, playerRotationLength / 0.4D));
                playerVelocity = this.getVelocity().add(0.0D, gravity * (-1.0D + (double) playerCosPitch * 0.75D), 0.0D);
                double playerHorizontalVelocityLength1;
                if (playerVelocity.y < 0.0D && playerHorizontalRotationLength > 0.0D) {
                    playerHorizontalVelocityLength1 = playerVelocity.y * -0.1D * (double) playerCosPitch;
                    playerVelocity = playerVelocity.add(playerRotationVector.x * playerHorizontalVelocityLength1 / playerHorizontalRotationLength, playerHorizontalVelocityLength1, playerRotationVector.z * playerHorizontalVelocityLength1 / playerHorizontalRotationLength);
                }

                if (playerPitch < 0.0F && playerHorizontalRotationLength > 0.0D) {
                    playerHorizontalVelocityLength1 = playerHorizontalVelocityLength * (double)(-MathHelper.sin(playerPitch)) * 0.04D;
                    playerVelocity = playerVelocity.add(-playerRotationVector.x * playerHorizontalVelocityLength1 / playerHorizontalRotationLength, playerHorizontalVelocityLength1 * 3.2D, -playerRotationVector.z * playerHorizontalVelocityLength1 / playerHorizontalRotationLength);
                }

                if (playerHorizontalRotationLength > 0.0D) {
                    playerVelocity = playerVelocity.add((playerRotationVector.x / playerHorizontalRotationLength * playerHorizontalVelocityLength - playerVelocity.x) * 0.1D, 0.0D, (playerRotationVector.z / playerHorizontalRotationLength * playerHorizontalVelocityLength - playerVelocity.z) * 0.1D);
                }

                this.setVelocity(playerVelocity.multiply(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
                this.move(MovementType.SELF, RotationUtil.vecPlayerToWorld(this.getVelocity(), gravityDirection));
                if (this.horizontalCollision && !this.world.isClient) {
                    playerHorizontalVelocityLength1 = this.getVelocity().horizontalLength();
                    double horizontalVelocityDelta = playerHorizontalVelocityLength - playerHorizontalVelocityLength1;
                    float damage = (float)(horizontalVelocityDelta * 10.0D - 3.0D);
                    if (damage > 0.0F) {
                        this.playSound(this.getFallSound((int) damage), 1.0F, 1.0F);
                        this.damage(DamageSource.FLY_INTO_WALL, damage);
                    }
                }

                if (this.onGround && !this.world.isClient) {
                    this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, false);
                }
            } else {
                BlockPos velocityAffectingPos = this.getVelocityAffectingPos();
                float slipperiness = this.world.getBlockState(velocityAffectingPos).getBlock().getSlipperiness();
                movementSpeedMultiplier = this.onGround ? slipperiness * 0.91F : 0.91F;
                Vec3d playerVelocity = this.method_26318(movementInput, slipperiness);
                double playerVelocityY = playerVelocity.y;
                if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
                    playerVelocityY += (0.05D * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - playerVelocity.y) * 0.2D;
                    this.fallDistance = 0.0F;
                } else if (this.world.isClient && !this.world.isChunkLoaded(velocityAffectingPos)) {
                    if (this.getY() > (double)this.world.getBottomY()) {
                        playerVelocityY = -0.1D;
                    } else {
                        playerVelocityY = 0.0D;
                    }
                } else if (!this.hasNoGravity()) {
                    playerVelocityY -= gravity;
                }

                if (this.hasNoDrag()) {
                    this.setVelocity(playerVelocity.x, playerVelocityY, playerVelocity.z);
                } else {
                    this.setVelocity(playerVelocity.x * (double) movementSpeedMultiplier, playerVelocityY * 0.9800000190734863D, playerVelocity.z * (double) movementSpeedMultiplier);
                }
            }
        }

        this.updateLimbs((LivingEntity)(Object) this, this instanceof Flutterer);
    }

    @ModifyArg(
            method = "method_26318",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private Vec3d modify_method_26318_move_1(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    @ModifyArg(
            method = "playBlockFallSound",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                    ordinal = 0
            ) ,
            index = 0
    )
    private BlockPos modify_playBlockFallSound_getBlockState_0(BlockPos blockPos) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return blockPos;
        }

        return new BlockPos(this.getPos().add(RotationUtil.vecPlayerToWorld(0, -0.20000000298023224D, 0, gravityDirection)));
    }

    @Redirect(
            method = "canSee",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/util/math/Vec3d",
                    ordinal = 0
            )
    )
    private Vec3d redirect_canSee_new_0(double x, double y, double z) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return new Vec3d(x, y, z);
        }

        return this.getEyePos();
    }

    @Redirect(
            method = "canSee",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/util/math/Vec3d",
                    ordinal = 1
            )
    )
    private Vec3d redirect_canSee_new_1(double x, double y, double z, Entity entity) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return new Vec3d(x, y, z);
        }

        return entity.getEyePos();
    }

    @Inject(
            method = "getBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_getBoundingBox(EntityPose pose, CallbackInfoReturnable<Box> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Box box = cir.getReturnValue();
        if(gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.offset(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection));
    }

    @Inject(
            method = "updateLimbs",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_updateLimbs(LivingEntity entity, boolean flutter, CallbackInfo ci) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        ci.cancel();

        Vec3d playerPosDelta = RotationUtil.vecWorldToPlayer(entity.getX() - entity.prevX, entity.getY() - entity.prevY, entity.getZ() - entity.prevZ, gravityDirection);

        entity.lastLimbDistance = entity.limbDistance;
        double d = playerPosDelta.x;
        double e = flutter ? playerPosDelta.y : 0.0D;
        double f = playerPosDelta.z;
        float g = (float)Math.sqrt(d * d + e * e + f * f) * 4.0F;
        if (g > 1.0F) {
            g = 1.0F;
        }

        entity.limbDistance += (g - entity.limbDistance) * 0.4F;
        entity.limbAngle += entity.limbDistance;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getX_0(LivingEntity livingEntity) {
        Direction gravityDirection = ((EntityAccessor) livingEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getX();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getX() - livingEntity.prevX, livingEntity.getY() - livingEntity.prevY, livingEntity.getZ() - livingEntity.prevZ, gravityDirection).x + livingEntity.prevX;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getZ_0(LivingEntity livingEntity) {
        Direction gravityDirection = ((EntityAccessor) livingEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getX();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getX() - livingEntity.prevX, livingEntity.getY() - livingEntity.prevY, livingEntity.getZ() - livingEntity.prevZ, gravityDirection).z + livingEntity.prevZ;
    }

    @Redirect(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_damage_getX_0(Entity attacker) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            if(((EntityAccessor) attacker).gravitychanger$getAppliedGravityDirection() == Direction.DOWN) {
                return attacker.getX();
            } else {
                return attacker.getEyePos().x;
            }
        }

        return RotationUtil.vecWorldToPlayer(attacker.getEyePos(), gravityDirection).x;
    }

    @Redirect(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_damage_getZ_0(Entity attacker) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            if(((EntityAccessor) attacker).gravitychanger$getAppliedGravityDirection() == Direction.DOWN) {
                return attacker.getZ();
            } else {
                return attacker.getEyePos().z;
            }
        }

        return RotationUtil.vecWorldToPlayer(attacker.getEyePos(), gravityDirection).z;
    }

    @Redirect(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_damage_getX_0(LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return RotationUtil.vecWorldToPlayer(target.getPos(), gravityDirection).x;
    }

    @Redirect(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_damage_getZ_0(LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return target.getZ();
        }

        return RotationUtil.vecWorldToPlayer(target.getPos(), gravityDirection).z;
    }

    @Redirect(
            method = "knockback",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_knockback_getX_0(LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return RotationUtil.vecWorldToPlayer(target.getPos(), gravityDirection).x;
    }

    @Redirect(
            method = "knockback",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_knockback_getZ_0(LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return target.getZ();
        }

        return RotationUtil.vecWorldToPlayer(target.getPos(), gravityDirection).z;
    }

    @Redirect(
            method = "knockback",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getX()D",
                    ordinal = 1
            )
    )
    private double redirect_knockback_getX_1(LivingEntity attacker, LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            if(((EntityAccessor) attacker).gravitychanger$getAppliedGravityDirection() == Direction.DOWN) {
                return attacker.getX();
            } else {
                return attacker.getEyePos().x;
            }
        }

        return RotationUtil.vecWorldToPlayer(attacker.getEyePos(), gravityDirection).x;
    }

    @Redirect(
            method = "knockback",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
                    ordinal = 1
            )
    )
    private double redirect_knockback_getZ_1(LivingEntity attacker, LivingEntity target) {
        Direction gravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            if(((EntityAccessor) attacker).gravitychanger$getAppliedGravityDirection() == Direction.DOWN) {
                return attacker.getZ();
            } else {
                return attacker.getEyePos().z;
            }
        }

        return RotationUtil.vecWorldToPlayer(attacker.getEyePos(), gravityDirection).z;
    }

    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/util/math/BlockPos",
                    ordinal = 0
            )
    )
    private BlockPos redirect_baseTick_new_0(double x, double y, double z) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return new BlockPos(x, y, z);
        }

        return new BlockPos(this.getEyePos());
    }

    @Redirect(
            method = "spawnItemParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_spawnItemParticles_add_0(Vec3d vec3d, double x, double y, double z) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d.add(x, y, z);
        }

        return this.getEyePos().add(RotationUtil.vecPlayerToWorld(vec3d, gravityDirection));
    }

    @ModifyVariable(
            method = "spawnItemParticles",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/math/Vec3d;rotateY(F)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private Vec3d modify_spawnItemParticles_Vec3d_0(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    @ModifyArgs(
            method = "tickStatusEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V",
                    ordinal = 0
            )
    )
    private void modify_tickStatusEffects_addParticle_0(Args args) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Vec3d vec3d = this.getPos().subtract(RotationUtil.vecPlayerToWorld(this.getPos().subtract(args.get(1), args.get(2), args.get(3)), gravityDirection));
        args.set(1, vec3d.x);
        args.set(2, vec3d.y);
        args.set(3, vec3d.z);
    }

    @ModifyArgs(
            method = "addDeathParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V",
                    ordinal = 0
            )
    )
    private void modify_addDeathParticless_addParticle_0(Args args) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Vec3d vec3d = this.getPos().subtract(RotationUtil.vecPlayerToWorld(this.getPos().subtract(args.get(1), args.get(2), args.get(3)), gravityDirection));
        args.set(1, vec3d.x);
        args.set(2, vec3d.y);
        args.set(3, vec3d.z);
    }
}
