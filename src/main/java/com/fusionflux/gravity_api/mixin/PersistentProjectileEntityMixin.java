package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.accessor.EntityAccessor;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity implements EntityAccessor {
    //private static final TrackedData<Direction> gravitychanger$GRAVITY_DIRECTION = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.FACING);
//
    //private static final TrackedData<Direction> gravitychanger$DEFAULT_GRAVITY_DIRECTION = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.FACING);
//
    //private Direction gravitychanger$prevGravityDirection = Direction.DOWN;

    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


      @Override
  public Direction gravitychanger$getAppliedGravityDirection() {
      return GravityChangerAPI.getGravityDirection((PersistentProjectileEntity)(Object)this);
  }
//
//
//
   // @Override
   // public void gravitychanger$onGravityChanged(Direction prevGravityDirection, boolean initialGravity) {
   //     Direction gravityDirection = this.gravitychanger$getGravityDirection();
//
   //     this.fallDistance = 0;
//
   //     //this.setBoundingBox(this.calculateBoundingBox());
//
   //     if(!initialGravity) {
   //         // Adjust position to avoid suffocation in blocks when changing gravity
   //         EntityDimensions dimensions = this.getDimensions(this.getPose());
   //         Direction relativeDirection = RotationUtil.dirWorldToPlayer(gravityDirection, prevGravityDirection);
   //         Vec3d relativePosOffset = switch(relativeDirection) {
   //             case DOWN -> Vec3d.ZERO;
   //             case UP -> new Vec3d(0.0D, dimensions.height - 1.0E-6D, 0.0D);
   //             default -> Vec3d.of(relativeDirection.getVector()).multiply(dimensions.width / 2 - (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D)).add(0.0D, dimensions.width / 2 - (prevGravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D), 0.0D);
   //         };
   //         //this.setPosition(this.getPos().add(RotationUtil.vecPlayerToWorld(relativePosOffset, prevGravityDirection)));
   //         if(GravityChangerMod.config.worldVelocity)
   //         this.setVelocity(RotationUtil.vecWorldToPlayer(RotationUtil.vecPlayerToWorld(this.getVelocity(), prevGravityDirection), gravityDirection));
//
//
//
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
   //     this.getDataTracker().set(gravitychanger$GRAVITY_DIRECTION, gravityDirection);
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
   //     this.getDataTracker().set(gravitychanger$DEFAULT_GRAVITY_DIRECTION, gravityDirection);
   // }
//
   // @Override
   // public void gravitychanger$onTrackedData(TrackedData<?> data) {
   //     if(!this.world.isClient) return;
//
   //     if(gravitychanger$GRAVITY_DIRECTION.equals(data)) {
   //         Direction gravityDirection = this.gravitychanger$getGravityDirection();
   //         if(this.gravitychanger$prevGravityDirection != gravityDirection) {
   //             this.gravitychanger$onGravityChanged(this.gravitychanger$prevGravityDirection, true);
   //             this.gravitychanger$prevGravityDirection = gravityDirection;
   //         }
   //     }
   // }
//
   // @Inject(
   //         method = "initDataTracker",
   //         at = @At("RETURN")
   // )
   // private void inject_initDataTracker(CallbackInfo ci) {
   //     this.dataTracker.startTracking(gravitychanger$GRAVITY_DIRECTION, Direction.DOWN);
   //     this.dataTracker.startTracking(gravitychanger$DEFAULT_GRAVITY_DIRECTION, Direction.DOWN);
   // }
//
   // @Inject(
   //         method = "readCustomDataFromNbt",
   //         at = @At("RETURN")
   // )
   // private void inject_readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
   //     if(nbt.contains("GravityDirection", NbtElement.INT_TYPE)) {
   //         Direction gravityDirection = Direction.byId(nbt.getInt("GravityDirection"));
   //         this.gravitychanger$setGravityDirection(gravityDirection, true);
   //     }
   //     if(nbt.contains("DefaultGravityDirection", NbtElement.INT_TYPE)) {
   //         Direction gravityDirection = Direction.byId(nbt.getInt("DefaultGravityDirection"));
   //         this.gravitychanger$setDefaultGravityDirection(gravityDirection, true);
   //     }
   // }
//
   // @Inject(
   //         method = "writeCustomDataToNbt",
   //         at = @At("RETURN")
   // )
   // private void inject_writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
   //     nbt.putInt("GravityDirection", this.gravitychanger$getGravityDirection().getId());
   //     nbt.putInt("DefaultGravityDirection", this.gravitychanger$getDefaultGravityDirection().getId());
   // }



    @ModifyVariable(
            method = "tick",
            at = @At(
                    value = "STORE"
            )
            ,ordinal = 0
    )
    public Vec3d tick(Vec3d modify){
        modify = new Vec3d(modify.x, modify.y+0.05, modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify,((EntityAccessor) this).gravitychanger$getAppliedGravityDirection());
        modify = new Vec3d(modify.x, modify.y-0.05, modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify,((EntityAccessor) this).gravitychanger$getAppliedGravityDirection());
        return  modify;
    }


    @ModifyArgs(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;)V",
                    ordinal = 0
            )
    )
    private static void modifyargs_init_init_0(Args args, EntityType<? extends ThrownEntity> type, LivingEntity owner, World world) {
        Direction gravityDirection = ((EntityAccessor) owner).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        Vec3d pos = owner.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.10000000149011612D, 0.0D, gravityDirection));
        args.set(1, pos.x);
        args.set(2, pos.y);
        args.set(3, pos.z);
    }
}
