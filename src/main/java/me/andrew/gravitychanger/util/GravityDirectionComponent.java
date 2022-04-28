package me.andrew.gravitychanger.util;

import me.andrew.gravitychanger.GravityChangerMod;
import me.andrew.gravitychanger.mixin.AccessorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class GravityDirectionComponent implements GravityComponent{

    Direction gravityDirection = Direction.DOWN;
    Direction defaultGravityDirection = Direction.DOWN;
    Direction prevGravityDirection = Direction.DOWN;

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
            if(entity instanceof ServerPlayerEntity serverPlayerEntity) {
                if(serverPlayerEntity.networkHandler != null)
                    serverPlayerEntity.networkHandler.syncWithPlayerPosition();
            }

            // Keep world velocity when changing gravity
            if(GravityChangerMod.config.worldVelocity)
                entity.setVelocity(RotationUtil.vecWorldToPlayer(RotationUtil.vecPlayerToWorld(entity.getVelocity(), prevGravityDirection), gravityDirection));

            // Keep world looking direction when changing gravity
            if(GravityChangerMod.config.keepWorldLook) {
                Vec2f worldAngles = RotationUtil.rotPlayerToWorld(entity.getYaw(), entity.getPitch(), prevGravityDirection);
                Vec2f newViewAngles = RotationUtil.rotWorldToPlayer(worldAngles.x, worldAngles.y, gravityDirection);
                entity.setYaw(newViewAngles.x);
                entity.setPitch(newViewAngles.y);
            }else {
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
        }
    }

    @Override
    public Direction getTrackedGravityDirection() {
        return gravityDirection;
    }

    @Override
    public Direction getDefaultTrackedGravityDirection() {
        return defaultGravityDirection;
    }

    @Override
    public void setTrackedGravityDirection(Direction gravityDirection) {
        if (this.prevGravityDirection != gravityDirection) {
            this.onGravityChanged(this.prevGravityDirection, false);
            this.prevGravityDirection = gravityDirection;
        }
            this.gravityDirection = gravityDirection;
    }

    @Override
    public void setDefaultTrackedGravityDirection(Direction gravityDirection) {
            this.defaultGravityDirection = gravityDirection;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }
}
