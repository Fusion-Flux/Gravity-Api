package me.andrew.gravitychanger.mixin;

import com.google.common.collect.Lists;
import me.andrew.gravitychanger.GravityChangerMod;
import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(AreaEffectCloudEntity.class)
public abstract class AreaEffectCloudEntityMixin extends Entity implements EntityAccessor, RotatableEntityAccessor {


    @Shadow public abstract boolean isWaiting();

    @Shadow public abstract float getRadius();

    @Shadow public abstract ParticleEffect getParticleType();

    @Shadow public abstract int getColor();

    @Shadow private int duration;
    @Shadow private int waitTime;

    @Shadow protected abstract void setWaiting(boolean waiting);

    @Shadow private float radiusGrowth;

    @Shadow public abstract void setRadius(float radius);

    @Shadow @Final private Map<Entity, Integer> affectedEntities;
    @Shadow private Potion potion;
    @Shadow @Final private List<StatusEffectInstance> effects;
    @Shadow private int reapplicationDelay;

    @Shadow @Nullable public abstract LivingEntity getOwner();

    @Shadow private float radiusOnUse;
    @Shadow private int durationOnUse;
    private static final TrackedData<Direction> gravitychanger$GRAVITY_DIRECTION = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FACING);

    private static final TrackedData<Direction> gravitychanger$DEFAULT_GRAVITY_DIRECTION = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FACING);

    private Direction gravitychanger$prevGravityDirection = Direction.DOWN;

    public AreaEffectCloudEntityMixin(EntityType<?> type, World world) {
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
            Direction relativeDirection = RotationUtil.dirWorldToPlayer(gravityDirection, prevGravityDirection);
            Vec3d relativePosOffset = switch(relativeDirection) {
                case DOWN -> Vec3d.ZERO;
                case UP -> new Vec3d(0.0D, dimensions.height - 1.0E-6D, 0.0D);
                default -> Vec3d.of(relativeDirection.getVector()).multiply(dimensions.width / 2 - (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D)).add(0.0D, dimensions.width / 2 - (prevGravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D), 0.0D);
            };
            //this.setPosition(this.getPos().add(RotationUtil.vecPlayerToWorld(relativePosOffset, prevGravityDirection)));
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
        this.dataTracker.startTracking(gravitychanger$DEFAULT_GRAVITY_DIRECTION, Direction.DOWN);
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
        if(nbt.contains("DefaultGravityDirection", NbtElement.INT_TYPE)) {
            Direction gravityDirection = Direction.byId(nbt.getInt("DefaultGravityDirection"));
            this.gravitychanger$setDefaultGravityDirection(gravityDirection, true);
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





    @Overwrite
    public void tick() {
        super.tick();
        boolean bl = this.isWaiting();
        float f = this.getRadius();
        if (this.world.isClient) {
            if (bl && this.random.nextBoolean()) {
                return;
            }

            ParticleEffect particleEffect = this.getParticleType();
            int i;
            float g;
            if (bl) {
                i = 2;
                g = 0.2F;
            } else {
                i = MathHelper.ceil(3.1415927F * f * f);
                g = f;
            }

            for(int j = 0; j < i; ++j) {
                float h = this.random.nextFloat() * 6.2831855F;
                float k = MathHelper.sqrt(this.random.nextFloat()) * g;
                double d = this.getX() ;
                double e = this.getY();
                double l = this.getZ() ;
                Vec3d modify = RotationUtil.vecWorldToPlayer(d,e,l,gravitychanger$getGravityDirection());
                d = modify.x+ (double)(MathHelper.cos(h) * k);
                e = modify.y;
                l = modify.z+ (double)(MathHelper.sin(h) * k);
                modify = RotationUtil.vecPlayerToWorld(d,e,l,gravitychanger$getGravityDirection());
                d = modify.x;
                e = modify.y;
                l = modify.z;
                double n;
                double o;
                double p;
                if (particleEffect.getType() != ParticleTypes.ENTITY_EFFECT) {
                    if (bl) {
                        n = 0.0D;
                        o = 0.0D;
                        p = 0.0D;
                    } else {
                        n = (0.5D - this.random.nextDouble()) * 0.15D;
                        o = 0.009999999776482582D;
                        p = (0.5D - this.random.nextDouble()) * 0.15D;
                    }
                } else {
                    int m = bl && this.random.nextBoolean() ? 16777215 : this.getColor();
                    n = (double)((float)(m >> 16 & 255) / 255.0F);
                    o = (double)((float)(m >> 8 & 255) / 255.0F);
                    p = (double)((float)(m & 255) / 255.0F);
                }

                this.world.addImportantParticle(particleEffect, d, e, l, n, o, p);
            }
        } else {
            if (this.age >= this.waitTime + this.duration) {
                this.discard();
                return;
            }

            boolean bl2 = this.age < this.waitTime;
            if (bl != bl2) {
                this.setWaiting(bl2);
            }

            if (bl2) {
                return;
            }

            if (this.radiusGrowth != 0.0F) {
                f += this.radiusGrowth;
                if (f < 0.5F) {
                    this.discard();
                    return;
                }

                this.setRadius(f);
            }

            if (this.age % 5 == 0) {
                this.affectedEntities.entrySet().removeIf((entry) -> {
                    return this.age >= (Integer)entry.getValue();
                });
                List<StatusEffectInstance> list = Lists.newArrayList();
                Iterator var24 = this.potion.getEffects().iterator();

                while(var24.hasNext()) {
                    StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var24.next();
                    list.add(new StatusEffectInstance(statusEffectInstance.getEffectType(), statusEffectInstance.getDuration() / 4, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()));
                }

                list.addAll(this.effects);
                if (list.isEmpty()) {
                    this.affectedEntities.clear();
                } else {
                    List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
                    if (!list2.isEmpty()) {
                        Iterator var27 = list2.iterator();

                        while(true) {
                            double s;
                            LivingEntity livingEntity;
                            do {
                                do {
                                    do {
                                        if (!var27.hasNext()) {
                                            return;
                                        }

                                        livingEntity = (LivingEntity)var27.next();
                                    } while(this.affectedEntities.containsKey(livingEntity));
                                } while(!livingEntity.isAffectedBySplashPotions());

                                double q = livingEntity.getX() - this.getX();
                                double r = livingEntity.getZ() - this.getZ();
                                s = q * q + r * r;
                            } while(!(s <= (double)(f * f)));

                            this.affectedEntities.put(livingEntity, this.age + this.reapplicationDelay);
                            Iterator var14 = list.iterator();

                            while(var14.hasNext()) {
                                StatusEffectInstance statusEffectInstance2 = (StatusEffectInstance)var14.next();
                                if (statusEffectInstance2.getEffectType().isInstant()) {
                                    statusEffectInstance2.getEffectType().applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance2.getAmplifier(), 0.5D);
                                } else {
                                    livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance2), this);
                                }
                            }

                            if (this.radiusOnUse != 0.0F) {
                                f += this.radiusOnUse;
                                if (f < 0.5F) {
                                    this.discard();
                                    return;
                                }

                                this.setRadius(f);
                            }

                            if (this.durationOnUse != 0) {
                                this.duration += this.durationOnUse;
                                if (this.duration <= 0) {
                                    this.discard();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

    }


}
