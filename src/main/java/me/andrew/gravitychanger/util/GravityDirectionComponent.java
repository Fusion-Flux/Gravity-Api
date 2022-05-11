package me.andrew.gravitychanger.util;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.andrew.gravitychanger.GravityChangerMod;
import me.andrew.gravitychanger.accessor.ServerPlayerEntityAccessor;
import me.andrew.gravitychanger.mixin.AccessorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryEntry;

public class GravityDirectionComponent implements GravityComponent, AutoSyncedComponent {

    Direction gravityDirection = Direction.DOWN;
    Direction defaultGravityDirection = Direction.DOWN;
    Direction prevGravityDirection = Direction.DOWN;
    Direction trackedPrevGravityDirection = Direction.DOWN;

    private final Entity entity;
    public GravityDirectionComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void onGravityChanged(Direction prevGravityDirection, boolean initialGravity) {
        Direction gravityDirection = this.getTrackedGravityDirection();

        entity.fallDistance = 0;

        entity.setBoundingBox(((AccessorEntity)entity).gravity$calculateBoundingBox());

        if(!initialGravity) {
            // Adjust position to avoid suffocation in blocks when changing gravity
            EntityDimensions dimensions = entity.getDimensions(entity.getPose());
            Direction relativeDirection = RotationUtil.dirWorldToPlayer(gravityDirection, prevGravityDirection);
            if(!(entity instanceof EndCrystalEntity)) {
                Vec3d relativePosOffset = switch (relativeDirection) {
                    case DOWN -> Vec3d.ZERO;
                    case UP -> new Vec3d(0.0D, dimensions.height - 1.0E-6D, 0.0D);
                    default -> Vec3d.of(relativeDirection.getVector()).multiply(dimensions.width / 2 - (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D)).add(0.0D, dimensions.width / 2 - (prevGravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D), 0.0D);
                };
                entity.setPosition(entity.getPos().add(RotationUtil.vecPlayerToWorld(relativePosOffset, prevGravityDirection)));
            }else{
                entity.setPosition(entity.getPos().subtract(RotationUtil.vecWorldToPlayer(new Vec3d(0,(dimensions.height/2)+.5,0), prevGravityDirection)));
                Vec3d relativePosOffset = switch(relativeDirection) {
                    case DOWN -> Vec3d.ZERO;
                    case UP -> new Vec3d(0.0D, dimensions.height - 1.0E-6D, 0.0D);
                    default -> Vec3d.of(relativeDirection.getVector()).multiply(dimensions.width / 2 - (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D)).add(0.0D, dimensions.width / 2 - (prevGravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D), 0.0D);
                };
                entity.setPosition(entity.getPos().add(RotationUtil.vecPlayerToWorld(relativePosOffset, prevGravityDirection)).add(RotationUtil.vecPlayerToWorld(new Vec3d(0,(dimensions.height/2)+.5,0), gravityDirection)));
            }


            // Keep world velocity when changing gravity
            //if(!GravityChangerMod.config.worldVelocity)
            //    if(entity.isLogicalSideForUpdatingMovement())
            //    entity.setVelocity(RotationUtil.vecPlayerToWorld(RotationUtil.vecWorldToPlayer(entity.getVelocity(), prevGravityDirection), gravityDirection));

            if(!(entity instanceof ProjectileEntity)){
                entity.setVelocity(RotationUtil.vecWorldToPlayer(RotationUtil.vecPlayerToWorld(entity.getVelocity(),prevGravityDirection),gravityDirection));
            }

            // Keep world looking direction when changing gravity
            if(GravityChangerMod.config.keepWorldLook) {
                Vec2f worldAngles = RotationUtil.rotPlayerToWorld(entity.getYaw(), entity.getPitch(), prevGravityDirection);
                Vec2f newViewAngles = RotationUtil.rotWorldToPlayer(worldAngles.x, worldAngles.y, gravityDirection);
                entity.setYaw(newViewAngles.x);
                entity.setPitch(newViewAngles.y);
            }else {
                if (prevGravityDirection == gravityDirection.getOpposite()){
                    Vec2f worldAngles = RotationUtil.rotPlayerToWorld(entity.getYaw(), entity.getPitch(), prevGravityDirection);
                    Vec2f newViewAngles = RotationUtil.rotWorldToPlayer(worldAngles.x, worldAngles.y, gravityDirection);
                    entity.setYaw(newViewAngles.x);
                    entity.setPitch(newViewAngles.y);
                }


                if (prevGravityDirection == Direction.UP || prevGravityDirection == Direction.DOWN) {
                    if (gravityDirection == Direction.EAST) {
                        entity.setYaw(entity.getYaw() - 90);
                    }
                }

                if (prevGravityDirection == Direction.EAST) {
                    if (gravityDirection == Direction.UP || gravityDirection == Direction.DOWN) {
                        entity.setYaw(entity.getYaw() + 90);
                    }
                }

                if (prevGravityDirection == Direction.UP || prevGravityDirection == Direction.DOWN) {
                    if (gravityDirection == Direction.WEST) {
                        entity.setYaw(entity.getYaw() + 90);
                    }
                }

                if (prevGravityDirection == Direction.WEST) {
                    if (gravityDirection == Direction.UP || gravityDirection == Direction.DOWN) {
                        entity.setYaw(entity.getYaw() - 90);
                    }
                }

                if (prevGravityDirection == Direction.DOWN) {
                    if (gravityDirection == Direction.SOUTH) {
                        entity.setYaw(entity.getYaw() - 180);
                    }
                }

                if (prevGravityDirection == Direction.UP) {
                    if (gravityDirection == Direction.NORTH) {
                        entity.setYaw(entity.getYaw() - 180);
                    }
                }

                if (prevGravityDirection == Direction.SOUTH) {
                    if (gravityDirection == Direction.DOWN) {
                        entity.setYaw(entity.getYaw() + 180);
                    }
                }

                if (prevGravityDirection == Direction.NORTH) {
                    if (gravityDirection == Direction.UP) {
                        entity.setYaw(entity.getYaw() + 180);
                    }
                }
            }
        GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }

    @Override
    public Direction getTrackedGravityDirection() {
        if (!entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITIES)) {
            return gravityDirection;
        }
        return Direction.DOWN;
    }

    float test =0;

    @Override
    public Direction getPrevTrackedGravityDirection() {
        if (!entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITIES)) {
            return trackedPrevGravityDirection;
        }
        return Direction.DOWN;
    }


    @Override
    public Direction getDefaultTrackedGravityDirection() {
        if (!entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITIES)) {
            return defaultGravityDirection;
        }
        return Direction.DOWN;
    }



    @Override
    public void setTrackedGravityDirection(Direction gravityDirection, boolean initalGravity) {
        if(!entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITIES)) {
            if (this.prevGravityDirection != gravityDirection) {
                //Direction SavedGravity= this.gravityDirection;
                if(entity.world.isClient) {
                    if(entity instanceof PlayerEntity player && player.isMainPlayer())
                    RotationUtil.applyNewRotation(gravityDirection,this.gravityDirection);
                }
                setPrevTrackedGravityDirection(this.gravityDirection);
                this.gravityDirection = gravityDirection;
                this.onGravityChanged(this.prevGravityDirection, initalGravity);
                //setPrevTrackedGravityDirection(SavedGravity);
                this.prevGravityDirection = gravityDirection;
            }
            this.gravityDirection = gravityDirection;
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }

    @Override
    public void setPrevTrackedGravityDirection(Direction gravityDirection) {
        if(!entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITIES)) {
            this.trackedPrevGravityDirection = gravityDirection;
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }

    @Override
    public void setDefaultTrackedGravityDirection(Direction gravityDirection) {
        if(!entity.getType().getRegistryEntry().isIn(EntityTags.FORBIDDEN_ENTITIES)) {
            this.defaultGravityDirection = gravityDirection;
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }


    @Override
    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("GravityDirection", NbtElement.INT_TYPE)) {
            Direction gravityDirection = Direction.byId(nbt.getInt("GravityDirection"));
            this.setTrackedGravityDirection(gravityDirection,true);
        }
        if (nbt.contains("PrevGravityDirection", NbtElement.INT_TYPE)) {
            Direction gravityDirection = Direction.byId(nbt.getInt("PrevGravityDirection"));
            this.setPrevTrackedGravityDirection(gravityDirection);
        }
        if (nbt.contains("DefaultGravityDirection", NbtElement.INT_TYPE)) {
            Direction gravityDirection = Direction.byId(nbt.getInt("DefaultGravityDirection"));
            this.setDefaultTrackedGravityDirection(gravityDirection);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putInt("GravityDirection", this.getTrackedGravityDirection().getId());
        nbt.putInt("PrevGravityDirection", this.getPrevTrackedGravityDirection().getId());
        nbt.putInt("DefaultGravityDirection", this.getDefaultTrackedGravityDirection().getId());
    }
}
