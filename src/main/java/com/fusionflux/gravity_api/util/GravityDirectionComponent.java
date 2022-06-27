package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.RotationAnimation;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class GravityDirectionComponent implements GravityComponent, AutoSyncedComponent {
    Direction gravityDirection = Direction.DOWN;
    Direction defaultGravityDirection = Direction.DOWN;
    Direction prevGravityDirection = Direction.DOWN;
    boolean initalSpawn = true;
    boolean isInverted = false;
    int animationDuration = 500;
    RotationAnimation animation = new RotationAnimation();
    
    ArrayList<Gravity> gravityList = new ArrayList<>();
    
    private final Entity entity;
    
    public GravityDirectionComponent(Entity entity) {
        this.entity = entity;
    }
    
    public void onGravityChanged(Direction oldGravity, Direction newGravity, boolean initialGravity) {
        entity.fallDistance = 0;
        entity.setPosition(entity.getPos());//Causes bounding box recalculation
        
        if (!initialGravity) {
            adjustEntityPosition(oldGravity, newGravity);
        }
        
        // Keep world velocity when changing gravity
        if(entity instanceof PlayerEntity) {
            if (oldGravity.getOpposite() != newGravity) {
                entity.setVelocity(RotationUtil.vecPlayerToWorld(
                        RotationUtil.vecWorldToPlayer(entity.getVelocity().multiply(-1), oldGravity), newGravity)
                );
            } else {
                entity.setVelocity(RotationUtil.vecPlayerToWorld(
                        RotationUtil.vecWorldToPlayer(entity.getVelocity(), oldGravity), newGravity)
                );
            }
        }
    }
    
    // Adjust position to avoid suffocation in blocks when changing gravity
    private void adjustEntityPosition(Direction oldGravity, Direction newGravity) {
        if (entity instanceof AreaEffectCloudEntity || entity instanceof PersistentProjectileEntity || entity instanceof EndCrystalEntity) {
            return;
        }
        
        Box entityBoundingBox = entity.getBoundingBox();
        
        // for example, if gravity changed from down to north, move up
        // if gravity changed from down to up, also move up
        Direction movingDirection = oldGravity.getOpposite();
        
        Iterable<VoxelShape> collisions = entity.world.getCollisions(entity, entityBoundingBox);
        Box totalCollisionBox = null;
        for (VoxelShape collision : collisions) {
            if (!collision.isEmpty()) {
                Box boundingBox = collision.getBoundingBox();
                if (totalCollisionBox == null) {
                    totalCollisionBox = boundingBox;
                }
                else {
                    totalCollisionBox = totalCollisionBox.union(boundingBox);
                }
            }
        }
        
        if (totalCollisionBox != null) {
            entity.setPosition(entity.getPos().add(getPositionAdjustmentOffset(
                entityBoundingBox, totalCollisionBox, movingDirection
            )));
        }
    }
    
    private static Vec3d getPositionAdjustmentOffset(
        Box entityBoundingBox, Box nearbyCollisionUnion, Direction movingDirection
    ) {
        Direction.Axis axis = movingDirection.getAxis();
        double offset = 0;
        if (movingDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            double pushing = nearbyCollisionUnion.getMax(axis);
            double pushed = entityBoundingBox.getMin(axis);
            if (pushing > pushed) {
                offset = pushing - pushed;
            }
        }
        else {
            double pushing = nearbyCollisionUnion.getMin(axis);
            double pushed = entityBoundingBox.getMax(axis);
            if (pushing < pushed) {
                offset = pushed - pushing;
            }
        }
        
        return new Vec3d(movingDirection.getUnitVector()).multiply(offset);
    }
    
    @Override
    public Direction getGravityDirection() {
        if (canChangeGravity()) {
            return gravityDirection;
        }
        return Direction.DOWN;
    }
    
    private boolean canChangeGravity() {
        return EntityTags.canChangeGravity(entity);
    }
    
    @Override
    public Direction getPrevGravityDirection() {
        if (canChangeGravity()) {
            return prevGravityDirection;
        }
        return Direction.DOWN;
    }
    
    @Override
    public Direction getDefaultGravityDirection() {
        if (canChangeGravity()) {
            return defaultGravityDirection;
        }
        return Direction.DOWN;
    }
    
    @Override
    public void updateGravity(boolean initialGravity) {
        if (canChangeGravity()) {
            Direction newGravity = getActualGravityDirection();
            Direction oldGravity = gravityDirection;
            if (oldGravity != newGravity) {
                long timeMs = entity.world.getTime() * 50;
                animation.applyRotationAnimation(
                    newGravity, oldGravity,
                    initialGravity ? 0 : animationDuration,
                    entity, timeMs
                );
                prevGravityDirection = oldGravity;
                gravityDirection = newGravity;
                onGravityChanged(oldGravity, newGravity, initialGravity);
            }
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }
    
    @Override
    public Direction getActualGravityDirection() {
        Gravity primaryGravity = null;
        for (Gravity temp : gravityList) {
            if (primaryGravity != null) {
                if (temp.priority > primaryGravity.priority) {
                    primaryGravity = temp;
                }
            }
            else {
                primaryGravity = temp;
            }
        }
        Direction newGravity = getDefaultGravityDirection();
        if (primaryGravity != null) {
            newGravity = primaryGravity.gravityDirection;
        }
        if (isInverted) {
            newGravity = newGravity.getOpposite();
        }
        return newGravity;
    }
    
    @Override
    public void setDefaultGravityDirection(Direction gravityDirection, int animationDurationMs) {
        if (canChangeGravity()) {
            this.defaultGravityDirection = gravityDirection;
            this.animationDuration = animationDurationMs;
            this.updateGravity(false);
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }
    
    @Override
    public void addGravity(Gravity gravity, boolean initialGravity) {
        if (canChangeGravity()) {
            int index = 0;
            boolean addValue = true;
            for (Gravity temp : gravityList) {
                if (Objects.equals(temp.source, gravity.source)) {
                    gravityList.set(index, gravity);
                    addValue = false;
                    break;
                }
                index++;
            }
            if (addValue)
                this.gravityList.add(gravity);
            //this.updateGravity(initialGravity);
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }
    
    @Override
    public ArrayList<Gravity> getGravity() {
        return gravityList;
    }
    
    @Override
    public void setGravity(ArrayList<Gravity> gravityList, boolean initalGravity) {
        this.gravityList = gravityList;
        //this.updateGravity(initalGravity);
        GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        //this.updateGravity(initalGravity);
    }
    
    @Override
    public void invertGravity(boolean isInverted) {
        this.isInverted = isInverted;
        this.updateGravity(false);
        GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
    }
    
    @Override
    public boolean getInvertGravity() {
        return this.isInverted;
    }
    
    @Override
    public void clearGravity() {
        this.gravityList = new ArrayList<Gravity>();
        this.updateGravity(false);
        GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        //this.updateGravity(false);
    }
    
    @Override
    public RotationAnimation getGravityAnimation() {
        return animation;
    }
    
    @Override
    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("ListSize", NbtElement.INT_TYPE)) {
            int listSize = nbt.getInt("ListSize");
            ArrayList<Gravity> newGravityList = new ArrayList<Gravity>();
            if (listSize != 0) {
                for (int index = 0; index < listSize; index++) {
                    Gravity newGravity = new Gravity(
                        Direction.byId(nbt.getInt("GravityDirection " + index)),
                        nbt.getInt("GravityPriority " + index),
                        nbt.getInt("GravityDuration " + index),
                        nbt.getString("GravitySource " + index)
                    );
                    newGravityList.add(newGravity);
                }
            }
            this.gravityList = (newGravityList);
        }
        if (nbt.contains("PrevGravityDirection", NbtElement.INT_TYPE)) {
            this.prevGravityDirection = (Direction.byId(nbt.getInt("PrevGravityDirection")));
        }
        if (nbt.contains("DefaultGravityDirection", NbtElement.INT_TYPE)) {
            this.defaultGravityDirection = (Direction.byId(nbt.getInt("DefaultGravityDirection")));
        }
        this.isInverted = (nbt.getBoolean("IsGravityInverted"));
        this.animationDuration = nbt.getInt("animationTimeMs");
        this.animation.fromNbt(nbt);
        
        this.updateGravity(initalSpawn);
        
        if (initalSpawn) {
            initalSpawn = false;
        }
        
    }
    
    @Override
    public void writeToNbt(@NotNull NbtCompound nbt) {
        int index = 0;
        if (!this.getGravity().isEmpty())
            for (Gravity temp : this.getGravity()) {
                if (temp.gravityDirection != null)
                    nbt.putInt("GravityDirection " + index, temp.getGravityDirection().getId());
                nbt.putInt("GravityPriority " + index, temp.getPriority());
                nbt.putInt("GravityDuration " + index, temp.getGravityDuration());
                if (temp.source != null)
                    nbt.putString("GravitySource " + index, temp.getSource());
                index++;
            }
        nbt.putInt("ListSize", index);
        nbt.putInt("PrevGravityDirection", this.getPrevGravityDirection().getId());
        nbt.putInt("DefaultGravityDirection", this.getDefaultGravityDirection().getId());
        nbt.putBoolean("IsGravityInverted", this.getInvertGravity());
        nbt.putInt("animationTimeMs", animationDuration);
        this.animation.toNbt(nbt);
    }
}
