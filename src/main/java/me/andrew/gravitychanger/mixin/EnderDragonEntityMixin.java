package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.GravityChangerMod;
import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends Entity implements EntityAccessor, RotatableEntityAccessor {

    private static final TrackedData<Direction> gravitychanger$GRAVITY_DIRECTION = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.FACING);

    private static final TrackedData<Direction> gravitychanger$DEFAULT_GRAVITY_DIRECTION = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.FACING);

    private Direction gravitychanger$prevGravityDirection = Direction.DOWN;

    public EnderDragonEntityMixin(EntityType<?> type, World world) {
        super(type, world);
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
    public void gravitychanger$onGravityChanged(Direction prevGravityDirection, boolean initialGravity) {
        Direction gravityDirection = this.gravitychanger$getGravityDirection();

        this.fallDistance = 0;

        this.setBoundingBox(this.calculateBoundingBox());

        if(!initialGravity) {
            // Adjust position to avoid suffocation in blocks when changing gravity
            EntityDimensions dimensions = this.getDimensions(this.getPose());
            //this.setPosition(this.getPos().subtract(RotationUtil.vecWorldToPlayer(new Vec3d(0,(dimensions.height/2)+.5,0), prevGravityDirection)));
            Direction relativeDirection = RotationUtil.dirWorldToPlayer(gravityDirection, prevGravityDirection);
            Vec3d relativePosOffset = switch(relativeDirection) {
                case DOWN -> Vec3d.ZERO;
                case UP -> new Vec3d(0.0D, dimensions.height - 1.0E-6D, 0.0D);
                default -> Vec3d.of(relativeDirection.getVector()).multiply(dimensions.width / 2 - (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D)).add(0.0D, dimensions.width / 2 - (prevGravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D), 0.0D);
            };
            //relativePosOffset = relativePosOffset.add(0,dimensions.height/2,0);
            //Vec3d test = RotationUtil.vecPlayerToWorld(RotationUtil.vecWorldToPlayer(new Vec3d(0,(dimensions.height/2)+.5,0), prevGravityDirection), gravityDirection);
            this.setPosition(this.getPos().add(RotationUtil.vecPlayerToWorld(relativePosOffset, prevGravityDirection)));
            if(GravityChangerMod.config.worldVelocity)
            this.setVelocity(RotationUtil.vecWorldToPlayer(RotationUtil.vecPlayerToWorld(this.getVelocity(), prevGravityDirection), gravityDirection));



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
    public Direction gravitychanger$getDefaultTrackedGravityDirection() {
        return this.getDataTracker().get(gravitychanger$DEFAULT_GRAVITY_DIRECTION);
    }

    @Override
    public void gravitychanger$setDefaultTrackedGravityDirection(Direction gravityDirection) {
        this.getDataTracker().set(gravitychanger$DEFAULT_GRAVITY_DIRECTION, gravityDirection);
    }

    @Override
    public void gravitychanger$onTrackedData(TrackedData<?> data) {
        if(this.world.isClient) return;

        if(gravitychanger$GRAVITY_DIRECTION.equals(data)) {
            Direction gravityDirection = this.gravitychanger$getGravityDirection();
            if(this.gravitychanger$prevGravityDirection != gravityDirection) {
                this.gravitychanger$onGravityChanged(this.gravitychanger$prevGravityDirection, false);
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
        this.dataTracker.startTracking(gravitychanger$DEFAULT_GRAVITY_DIRECTION, Direction.DOWN);
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("RETURN")
    )
    private void inject_readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if(nbt.contains("GravityDirection", NbtElement.INT_TYPE)) {
            Direction gravityDirection = Direction.byId(nbt.getInt("GravityDirection"));
            this.gravitychanger$setGravityDirection(gravityDirection, false);
        }
        if(nbt.contains("DefaultGravityDirection", NbtElement.INT_TYPE)) {
            Direction gravityDirection = Direction.byId(nbt.getInt("DefaultGravityDirection"));
            this.gravitychanger$setDefaultGravityDirection(gravityDirection, false);
        }
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("RETURN")
    )
    private void inject_writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("GravityDirection", this.gravitychanger$getGravityDirection().getId());
        nbt.putInt("DefaultGravityDirection", this.gravitychanger$getDefaultGravityDirection().getId());
    }



}
