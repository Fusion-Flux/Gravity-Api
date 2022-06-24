package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.mixin.AccessorEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"deprecation", "CommentedOutCode"})
public class GravityDirectionComponent implements GravityComponent, AutoSyncedComponent {
    Direction gravityDirection = Direction.DOWN;
    Direction defaultGravityDirection = Direction.DOWN;
    Direction prevGravityDirection = Direction.DOWN;
    Direction trackedPrevGravityDirection = Direction.DOWN;
    boolean initalSpawn = true;
    boolean isInverted = false;
    int animationTimeMs = 500;
    
    ArrayList<Gravity> gravityList = new ArrayList<Gravity>();
    
    private final Entity entity;
    
    public GravityDirectionComponent(Entity entity) {
        this.entity = entity;
    }
    
    @Override
    public void onGravityChanged(Direction prevGravityDirection, boolean initialGravity) {
        Direction gravityDirection = this.getTrackedGravityDirection();
        
        if (!initialGravity) {
            // keep the entity's eye position fixed, so it won't suffocate
            // this is also important to immersive portals rendering
            
            double eyeHeight = entity.getStandingEyeHeight();
            Vec3d prevEyeOffset = RotationUtil.vecPlayerToWorld(0, eyeHeight, 0, prevGravityDirection);
            Vec3d newEyeOffset = RotationUtil.vecPlayerToWorld(0, eyeHeight, 0, gravityDirection);
            
            entity.setPosition(entity.getPos().add(prevEyeOffset).subtract(newEyeOffset));
            
            // the entity position is interpolated between current position and last tick position
            // change the last tick position accordingly
            Vec3d prevFeetPosition = new Vec3d(entity.prevX, entity.prevY, entity.prevZ)
                .add(prevEyeOffset).subtract(newEyeOffset);
            entity.prevX = prevFeetPosition.x;
            entity.prevY = prevFeetPosition.y;
            entity.prevZ = prevFeetPosition.z;
        }
        
        entity.fallDistance = 0;
        entity.setBoundingBox(((AccessorEntity) entity).gravity$calculateBoundingBox());
        
        // Keep world velocity when changing gravity
        if (!GravityChangerMod.config.worldVelocity) {
            if (entity.isLogicalSideForUpdatingMovement()) {
                entity.setVelocity(RotationUtil.vecPlayerToWorld(
                    RotationUtil.vecWorldToPlayer(entity.getVelocity(), prevGravityDirection), gravityDirection)
                );
            }
        }
    }
    
    @Override
    public Direction getTrackedGravityDirection() {
        if (canChangeGravity()) {
            return gravityDirection;
        }
        return Direction.DOWN;
    }
    
    private boolean canChangeGravity() {
        return EntityTags.canChangeGravity(entity);
    }
    
    @Override
    public Direction getPrevTrackedGravityDirection() {
        if (canChangeGravity()) {
            return trackedPrevGravityDirection;
        }
        return Direction.DOWN;
    }
    
    @Override
    public Direction getDefaultTrackedGravityDirection() {
        if (canChangeGravity()) {
            return defaultGravityDirection;
        }
        return Direction.DOWN;
    }
    
    @Override
    public void updateGravity(boolean initialGravity) {
        if (canChangeGravity()) {
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
            Direction gravityDirection = this.getDefaultTrackedGravityDirection();
            if (primaryGravity != null) {
                gravityDirection = primaryGravity.gravityDirection;
            }
            if (isInverted) {
                gravityDirection = gravityDirection.getOpposite();
            }
            
            if (this.prevGravityDirection != gravityDirection) {
                
                if (entity.world.isClient && entity instanceof PlayerEntity player && player.isMainPlayer()) {
                    RotationAnimation.applyRotationAnimation(gravityDirection, this.gravityDirection, animationTimeMs);
                }
                setPrevTrackedGravityDirection(this.gravityDirection);
                this.gravityDirection = gravityDirection;
                this.onGravityChanged(this.prevGravityDirection, initialGravity);
                this.prevGravityDirection = gravityDirection;
            }
            this.gravityDirection = gravityDirection;
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }
    
    @Override
    public void setPrevTrackedGravityDirection(Direction gravityDirection) {
        if (canChangeGravity()) {
            if (gravityDirection != null) {
                this.trackedPrevGravityDirection = gravityDirection;
                GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
            }
        }
    }
    
    @Override
    public void setDefaultTrackedGravityDirection(Direction gravityDirection, int animationDurationMs) {
        if (canChangeGravity()) {
            this.defaultGravityDirection = gravityDirection;
            this.animationTimeMs = animationDurationMs;
            this.updateGravity(false);
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }
    
    public void setInternalDefaultTrackedGravityDirection(Direction gravityDirection) {
        if (canChangeGravity()) {
            this.defaultGravityDirection = gravityDirection;
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
    
    public void internalSetGravity(ArrayList<Gravity> gravityList, boolean initalGravity) {
        this.gravityList = gravityList;
        GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
    }
    
    @Override
    public void invertGravity(boolean isInverted) {
        this.isInverted = isInverted;
        this.updateGravity(false);
        GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        //this.updateGravity(false);
    }
    
    public void internalInvertGravity(boolean isInverted) {
        this.isInverted = isInverted;
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
            this.trackedPrevGravityDirection = (Direction.byId(nbt.getInt("PrevGravityDirection")));
        }
        if (nbt.contains("DefaultGravityDirection", NbtElement.INT_TYPE)) {
            this.defaultGravityDirection = (Direction.byId(nbt.getInt("DefaultGravityDirection")));
        }
        this.isInverted = (nbt.getBoolean("IsGravityInverted"));
        this.animationTimeMs = nbt.getInt("animationTimeMs");
        
        this.updateGravity(initalSpawn);
        
        if (initalSpawn) {
            initalSpawn = false;
        }
        
    }
    
    @Override
    public void writeToNbt(@NotNull NbtCompound nbt) {
        //nbt.putInt("GravityDirection", this.getTrackedGravityDirection().getId());
        
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
        nbt.putInt("PrevGravityDirection", this.getPrevTrackedGravityDirection().getId());
        nbt.putInt("DefaultGravityDirection", this.getDefaultTrackedGravityDirection().getId());
        nbt.putBoolean("IsGravityInverted", this.getInvertGravity());
        nbt.putInt("animationTimeMs", animationTimeMs);
    }
}
