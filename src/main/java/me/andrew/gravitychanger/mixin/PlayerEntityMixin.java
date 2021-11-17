package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.GravityChangerMod;
import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements EntityAccessor, RotatableEntityAccessor {
    @Shadow @Final private PlayerAbilities abilities;

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow protected abstract boolean clipAtLedge();

    @Shadow protected abstract boolean method_30263();

    private static final TrackedData<Direction> gravitychanger$GRAVITY_DIRECTION = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FACING);

    private Direction gravitychanger$prevGravityDirection = Direction.DOWN;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        Entity vehicle = this.getVehicle();
        if(vehicle != null) {
            return ((EntityAccessor) vehicle).gravitychanger$getAppliedGravityDirection();
        }

        return this.gravitychanger$getGravityDirection();
    }

    @Override
    public void gravitychanger$onGravityChanged(Direction prevGravityDirection, boolean initialGraity) {
        Direction gravityDirection = this.gravitychanger$getGravityDirection();

        this.fallDistance = 0;

        this.setBoundingBox(this.calculateBoundingBox());

        if(!initialGraity) {
            // Adjust position to avoid suffocation in blocks when changing gravity
            EntityDimensions dimensions = this.getDimensions(this.getPose());
            Direction relativeDirection = RotationUtil.dirWorldToPlayer(gravityDirection, prevGravityDirection);
            Vec3d relativePosOffset = switch(relativeDirection) {
                case DOWN -> Vec3d.ZERO;
                case UP -> new Vec3d(0.0D, dimensions.height - 1.0E-6D, 0.0D);
                default -> Vec3d.of(relativeDirection.getVector()).multiply(dimensions.width / 2 - (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D)).add(0.0D, dimensions.width / 2 - (prevGravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D), 0.0D);
            };
            this.setPosition(this.getPos().add(RotationUtil.vecPlayerToWorld(relativePosOffset, prevGravityDirection)));

            if((Object) this instanceof ServerPlayerEntity serverPlayerEntity) {
                serverPlayerEntity.networkHandler.syncWithPlayerPosition();
            }

            // Keep world velocity when changing gravity
            this.setVelocity(RotationUtil.vecWorldToPlayer(RotationUtil.vecPlayerToWorld(this.getVelocity(), prevGravityDirection), gravityDirection));

            // Keep world looking direction when changing gravity
            if(GravityChangerMod.config.keepWorldLook) {
                Vec2f worldAngles = RotationUtil.rotPlayerToWorld(this.getYaw(), this.getPitch(), prevGravityDirection);
                Vec2f newViewAngles = RotationUtil.rotWorldToPlayer(worldAngles.x, worldAngles.y, gravityDirection);
                this.setYaw(newViewAngles.x);
                this.setPitch(newViewAngles.y);
            }
        }
    }

    @Override
    public Direction gravitychanger$getTrackedGravityDirection() {
        return this.getDataTracker().get(gravitychanger$GRAVITY_DIRECTION);
    }

    @Override
    public void gravitychanger$setTrackedGravityDirection(Direction gravityDirection) {
        this.getDataTracker().set(gravitychanger$GRAVITY_DIRECTION, gravityDirection);
    }

    @Override
    public void gravitychanger$onTrackedData(TrackedData<?> data) {
        if(!this.world.isClient) return;

        if(gravitychanger$GRAVITY_DIRECTION.equals(data)) {
            Direction gravityDirection = this.gravitychanger$getGravityDirection();
            if(this.gravitychanger$prevGravityDirection != gravityDirection) {
                this.gravitychanger$onGravityChanged(this.gravitychanger$prevGravityDirection, true);
                this.gravitychanger$prevGravityDirection = gravityDirection;
            }
        }
    }

    @Inject(
            method = "initDataTracker",
            at = @At("RETURN")
    )
    private void inject_initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(gravitychanger$GRAVITY_DIRECTION, Direction.DOWN);
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("RETURN")
    )
    private void inject_readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if(nbt.contains("GravityDirection", NbtElement.INT_TYPE)) {
            Direction gravityDirection = Direction.byId(nbt.getInt("GravityDirection"));
            this.gravitychanger$setGravityDirection(gravityDirection, true);
        }
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("RETURN")
    )
    private void inject_writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("GravityDirection", this.gravitychanger$getGravityDirection().getId());
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_travel_getRotationVector_0(PlayerEntity playerEntity) {
        Direction gravityDirection = ((EntityAccessor) playerEntity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return playerEntity.getRotationVector();
        }

        return RotationUtil.vecWorldToPlayer(playerEntity.getRotationVector(), gravityDirection);
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "NEW",
                    target = "(DDD)Lnet/minecraft/util/math/BlockPos;",
                    ordinal = 0
            )
    )
    private BlockPos redirect_travel_new_0(double x, double y, double z) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return new BlockPos(x, y, z);
        }

        return new BlockPos(this.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, 1.0D - 0.1D, 0.0D, gravityDirection)));
    }

    @Redirect(
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/entity/ItemEntity",
                    ordinal = 0
            )
    )
    private ItemEntity redirect_dropItem_new_0(World world, double x, double y, double z, ItemStack stack) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return new ItemEntity(world, x, y, z, stack);
        }

        Vec3d vec3d = this.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.30000001192092896D, 0.0D, gravityDirection));

        return new ItemEntity(world, vec3d.x, vec3d.y, vec3d.z, stack);
    }

    @Redirect(
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V"
            )
    )
    private void redirect_dropItem_setVelocity(ItemEntity itemEntity, double x, double y, double z) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            itemEntity.setVelocity(x, y, z);
            return;
        }

        itemEntity.setVelocity(RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection));
    }

    @Inject(
            method = "adjustMovementForSneaking",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_adjustMovementForSneaking(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Vec3d playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);

        if (!this.abilities.flying && (type == MovementType.SELF || type == MovementType.PLAYER) && this.clipAtLedge() && this.method_30263()) {
            double d = playerMovement.x;
            double e = playerMovement.z;
            double var7 = 0.05D;

            while(d != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(RotationUtil.vecPlayerToWorld(d, (double)(-this.stepHeight), 0.0D, gravityDirection)))) {
                if (d < 0.05D && d >= -0.05D) {
                    d = 0.0D;
                } else if (d > 0.0D) {
                    d -= 0.05D;
                } else {
                    d += 0.05D;
                }
            }

            while(e != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(RotationUtil.vecPlayerToWorld(0.0D, (double)(-this.stepHeight), e, gravityDirection)))) {
                if (e < 0.05D && e >= -0.05D) {
                    e = 0.0D;
                } else if (e > 0.0D) {
                    e -= 0.05D;
                } else {
                    e += 0.05D;
                }
            }

            while(d != 0.0D && e != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(RotationUtil.vecPlayerToWorld(d, (double)(-this.stepHeight), e, gravityDirection)))) {
                if (d < 0.05D && d >= -0.05D) {
                    d = 0.0D;
                } else if (d > 0.0D) {
                    d -= 0.05D;
                } else {
                    d += 0.05D;
                }

                if (e < 0.05D && e >= -0.05D) {
                    e = 0.0D;
                } else if (e > 0.0D) {
                    e -= 0.05D;
                } else {
                    e += 0.05D;
                }
            }

            cir.setReturnValue(RotationUtil.vecPlayerToWorld(d, playerMovement.y, e, gravityDirection));
        } else {
            cir.setReturnValue(movement);
        }
    }

    @Redirect(
            method = "method_30263",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;offset(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            )
    )
    private Box redirect_method_30263_offset_0(Box box, double x, double y, double z) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return box.offset(x, y, z);
        }

        return box.offset(RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection));
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private float redirect_attack_getYaw_0(PlayerEntity attacker, Entity target) {
        Direction targetGravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        Direction attackerGravityDirection = ((EntityAccessor) attacker).gravitychanger$getAppliedGravityDirection();
        if(targetGravityDirection == attackerGravityDirection) {
            return attacker.getYaw();
        }

        return RotationUtil.rotWorldToPlayer(RotationUtil.rotPlayerToWorld(attacker.getYaw(), attacker.getPitch(), attackerGravityDirection), targetGravityDirection).x;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 1
            )
    )
    private float redirect_attack_getYaw_1(PlayerEntity attacker, Entity target) {
        Direction targetGravityDirection = ((EntityAccessor) target).gravitychanger$getAppliedGravityDirection();
        Direction attackerGravityDirection = ((EntityAccessor) attacker).gravitychanger$getAppliedGravityDirection();
        if(targetGravityDirection == attackerGravityDirection) {
            return attacker.getYaw();
        }

        return RotationUtil.rotWorldToPlayer(RotationUtil.rotPlayerToWorld(attacker.getYaw(), attacker.getPitch(), attackerGravityDirection), targetGravityDirection).x;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 2
            )
    )
    private float redirect_attack_getYaw_2(PlayerEntity attacker) {
        Direction gravityDirection = ((EntityAccessor) attacker).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return attacker.getYaw();
        }

        return RotationUtil.rotPlayerToWorld(attacker.getYaw(), attacker.getPitch(), gravityDirection).x;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 3
            )
    )
    private float redirect_attack_getYaw_3(PlayerEntity attacker) {
        Direction gravityDirection = ((EntityAccessor) attacker).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return attacker.getYaw();
        }

        return RotationUtil.rotPlayerToWorld(attacker.getYaw(), attacker.getPitch(), gravityDirection).x;
    }

    @ModifyArgs(
            method = "spawnParticles",
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

    @ModifyArgs(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            )
    )
    private void modify_tickMovement_expand_0(Args args) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Vec3d vec3d = RotationUtil.maskPlayerToWorld(args.get(0), args.get(1), args.get(2), gravityDirection);
        args.set(0, vec3d.x);
        args.set(1, vec3d.y);
        args.set(2, vec3d.z);
    }

    @ModifyArgs(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 1
            )
    )
    private void modify_tickMovement_expand_1(Args args) {
        Direction gravityDirection = ((EntityAccessor) this).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Vec3d vec3d = RotationUtil.maskPlayerToWorld(args.get(0), args.get(1), args.get(2), gravityDirection);
        args.set(0, vec3d.x);
        args.set(1, vec3d.y);
        args.set(2, vec3d.z);
    }
}
