package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityAccessor {
    @Shadow public abstract void readCustomDataFromNbt(NbtCompound nbt);

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow public abstract float getYaw(float tickDelta);

///private static final TrackedData<Direction> gravitychanger$GRAVITY_DIRECTION = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FACING);
///
///private static final TrackedData<Direction> gravitychanger$DEFAULT_GRAVITY_DIRECTION = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FACING);
///
///    private Direction gravitychanger$prevGravityDirection = Direction.DOWN;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


     @Override
     public Direction gravitychanger$getAppliedGravityDirection() {
         Entity vehicle = this.getVehicle();
         if(vehicle != null) {
             return ((EntityAccessor) vehicle).gravitychanger$getAppliedGravityDirection();
         }

         return GravityChangerAPI.getGravityDirection((LivingEntity)(Object)this);
     }
//
   // @Override
   // public void gravitychanger$onGravityChanged(Direction prevGravityDirection, boolean initialGravity) {
   //     LivingEntity testval = (LivingEntity) (Object) this;
   //     if(!(testval instanceof PlayerEntity)) {
   //         Direction gravityDirection = this.gravitychanger$getGravityDirection();
//
   //         this.fallDistance = 0;
//
   //         this.setBoundingBox(this.calculateBoundingBox());
//
   //         if (!initialGravity) {
   //             // Adjust position to avoid suffocation in blocks when changing gravity
   //             EntityDimensions dimensions = this.getDimensions(this.getPose());
   //             Direction relativeDirection = RotationUtil.dirWorldToPlayer(gravityDirection, prevGravityDirection);
   //             Vec3d relativePosOffset = switch (relativeDirection) {
   //                 case DOWN -> Vec3d.ZERO;
   //                 case UP -> new Vec3d(0.0D, dimensions.height - 1.0E-6D, 0.0D);
   //                 default -> Vec3d.of(relativeDirection.getVector()).multiply(dimensions.width / 2 - (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D)).add(0.0D, dimensions.width / 2 - (prevGravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D), 0.0D);
   //             };
   //             this.setPosition(this.getPos().add(RotationUtil.vecPlayerToWorld(relativePosOffset, prevGravityDirection)));
//
   //         /*if((Object) this instanceof LivingEntity serverPlayerEntity) {
   //             serverPlayerEntity.networkHandler.syncWithPlayerPosition();
   //         }*/
//
   //             // Keep world velocity when changing gravity
   //             if (GravityChangerMod.config.worldVelocity)
   //                 this.setVelocity(RotationUtil.vecWorldToPlayer(RotationUtil.vecPlayerToWorld(this.getVelocity(), prevGravityDirection), gravityDirection));
//
   //             // Keep world looking direction when changing gravity
   //             if (GravityChangerMod.config.keepWorldLook) {
   //                 Vec2f worldAngles = RotationUtil.rotPlayerToWorld(this.getYaw(), this.getPitch(), prevGravityDirection);
   //                 Vec2f newViewAngles = RotationUtil.rotWorldToPlayer(worldAngles.x, worldAngles.y, gravityDirection);
   //                 this.setYaw(newViewAngles.x);
   //                 this.setPitch(newViewAngles.y);
   //             } else {
   //                 if (prevGravityDirection == Direction.UP || prevGravityDirection == Direction.DOWN) {
   //                     if (gravityDirection == Direction.EAST) {
   //                         this.setYaw(this.getYaw() - 90);
   //                     }
   //                 }
//
   //                 if (prevGravityDirection == Direction.EAST) {
   //                     if (gravityDirection == Direction.UP || gravityDirection == Direction.DOWN) {
   //                         this.setYaw(this.getYaw() + 90);
   //                     }
   //                 }
//
   //                 if (prevGravityDirection == Direction.UP || prevGravityDirection == Direction.DOWN) {
   //                     if (gravityDirection == Direction.WEST) {
   //                         this.setYaw(this.getYaw() + 90);
   //                     }
   //                 }
//
   //                 if (prevGravityDirection == Direction.WEST) {
   //                     if (gravityDirection == Direction.UP || gravityDirection == Direction.DOWN) {
   //                         this.setYaw(this.getYaw() - 90);
   //                     }
   //                 }
//
   //                 if (prevGravityDirection == Direction.DOWN) {
   //                     if (gravityDirection == Direction.SOUTH) {
   //                         this.setYaw(this.getYaw() - 180);
   //                     }
   //                 }
//
   //                 if (prevGravityDirection == Direction.UP) {
   //                     if (gravityDirection == Direction.NORTH) {
   //                         this.setYaw(this.getYaw() - 180);
   //                     }
   //                 }
//
   //                 if (prevGravityDirection == Direction.SOUTH) {
   //                     if (gravityDirection == Direction.DOWN) {
   //                         this.setYaw(this.getYaw() + 180);
   //                     }
   //                 }
//
   //                 if (prevGravityDirection == Direction.NORTH) {
   //                     if (gravityDirection == Direction.UP) {
   //                         this.setYaw(this.getYaw() + 180);
   //                     }
   //                 }
   //             }
   //         }
   //     }
   // }
//
   // @Override
   // public Direction gravitychanger$getTrackedGravityDirection() {
   //     return this.getDataTracker().get(gravitychanger$GRAVITY_DIRECTION);
   // }
//
   // @Override
   // public void gravitychanger$setTrackedGravityDirection(Direction gravityDirection) {
   //     LivingEntity testval = (LivingEntity) (Object) this;
   //     if(!(testval instanceof PlayerEntity)) {
   //         this.getDataTracker().set(gravitychanger$GRAVITY_DIRECTION, gravityDirection);
   //     }
   // }
//
//
   // @Override
   // public Direction gravitychanger$getDefaultTrackedGravityDirection() {
   //     return this.getDataTracker().get(gravitychanger$DEFAULT_GRAVITY_DIRECTION);
   // }
//
   // @Override
   // public void gravitychanger$setDefaultTrackedGravityDirection(Direction gravityDirection) {
   //     LivingEntity testval = (LivingEntity) (Object) this;
   //     if(!(testval instanceof PlayerEntity)) {
   //         this.getDataTracker().set(gravitychanger$DEFAULT_GRAVITY_DIRECTION, gravityDirection);
   //     }
   // }
//
   // @Override
   // public void gravitychanger$onTrackedData(TrackedData<?> data) {
   //     if(this.world.isClient) return;
   //     LivingEntity testval = (LivingEntity) (Object) this;
   //     if(!(testval instanceof PlayerEntity)) {
   //         if (gravitychanger$GRAVITY_DIRECTION.equals(data)) {
   //             Direction gravityDirection = this.gravitychanger$getGravityDirection();
   //             if (this.gravitychanger$prevGravityDirection != gravityDirection) {
   //                 this.gravitychanger$onGravityChanged(this.gravitychanger$prevGravityDirection, false);
   //                 this.gravitychanger$prevGravityDirection = gravityDirection;
   //             }
   //         }
   //     }
   // }

  //  @Inject(
  //          method = "initDataTracker",
  //          at = @At("RETURN")
  //  )
  //  private void inject_initDataTracker(CallbackInfo ci) {
  //      LivingEntity testval = (LivingEntity) (Object) this;
  //      if(!(testval instanceof PlayerEntity)) {
  //          this.dataTracker.startTracking(gravitychanger$GRAVITY_DIRECTION, Direction.DOWN);
  //          this.dataTracker.startTracking(gravitychanger$DEFAULT_GRAVITY_DIRECTION, Direction.DOWN);
  //      }
  //  }
//
  //  @Inject(
  //          method = "readCustomDataFromNbt",
  //          at = @At("RETURN")
  //  )
  //  private void inject_readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
  //      LivingEntity testval = (LivingEntity) (Object) this;
  //      if(!(testval instanceof PlayerEntity)) {
  //          if (nbt.contains("GravityDirection", NbtElement.INT_TYPE)) {
  //              Direction gravityDirection = Direction.byId(nbt.getInt("GravityDirection"));
  //              this.gravitychanger$setGravityDirection(gravityDirection, false);
  //          }
  //          if (nbt.contains("DefaultGravityDirection", NbtElement.INT_TYPE)) {
  //              Direction gravityDirection = Direction.byId(nbt.getInt("DefaultGravityDirection"));
  //              this.gravitychanger$setDefaultGravityDirection(gravityDirection, false);
  //          }
  //      }
  //  }
//
  //  @Inject(
  //          method = "writeCustomDataToNbt",
  //          at = @At("RETURN")
  //  )
  //  private void inject_writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
  //      LivingEntity testval = (LivingEntity) (Object) this;
  //      if(!(testval instanceof PlayerEntity)) {
  //          nbt.putInt("GravityDirection", this.gravitychanger$getGravityDirection().getId());
  //          nbt.putInt("DefaultGravityDirection", this.gravitychanger$getDefaultGravityDirection().getId());
  //      }
  //  }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getY()D",
                    ordinal = 0
            )
    )
    private double redirect_travel_getY_0(LivingEntity livingEntity) {
        Direction gravityDirection = ((EntityAccessor) livingEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getY()D",
                    ordinal = 1
            )
    )
    private double redirect_travel_getY_1(LivingEntity livingEntity) {
        Direction gravityDirection = ((EntityAccessor) livingEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getY()D",
                    ordinal = 2
            )
    )
    private double redirect_travel_getY_2(LivingEntity livingEntity) {
        Direction gravityDirection = ((EntityAccessor) livingEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getY()D",
                    ordinal = 3
            )
    )
    private double redirect_travel_getY_3(LivingEntity livingEntity) {
        Direction gravityDirection = ((EntityAccessor) livingEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }

    @ModifyVariable(
            method = "travel",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 2
    )
    private Vec3d modify_travel_Vec3d_2(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
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
            return livingEntity.getZ();
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

    @ModifyVariable(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/LivingEntity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 1
    )
    private Vec3d modify_blockedByShield_Vec3d_1(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    @ModifyArg(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;relativize(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            index = 0
    )
    private Vec3d modify_blockedByShield_relativize_0(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return this.getEyePos();
    }

    @ModifyVariable(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/math/Vec3d;normalize()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 2
    )
    private Vec3d modify_blockedByShield_Vec3d_2(Vec3d vec3d) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
}
