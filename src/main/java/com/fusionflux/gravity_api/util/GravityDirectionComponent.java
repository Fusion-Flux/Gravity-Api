package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class GravityDirectionComponent implements GravityComponent {
    Direction gravityDirection = Direction.DOWN;
    Direction defaultGravityDirection = Direction.DOWN;

    double defaultGravityStrength = GravityChangerConfig.worldDefaultGravityStrength;
    Direction prevGravityDirection = Direction.DOWN;
    boolean isInverted = false;
    RotationAnimation animation = new RotationAnimation();
    boolean needsInitialSync = false;
    
    ArrayList<Gravity> gravityList = new ArrayList<>();
    
    private final Entity entity;
    
    public GravityDirectionComponent(Entity entity) {
        this.entity = entity;
    }
    
    public void onGravityChanged(Direction oldGravity, Direction newGravity, RotationParameters rotationParameters, boolean initialGravity) {
        entity.fallDistance = 0;
        entity.setPosition(entity.getPos());//Causes bounding box recalculation
        
        if (!initialGravity) {
            if(!(entity instanceof ServerPlayerEntity)) {
                //A relativeRotationCentre of zero will result in zero translation
                Vec3d relativeRotationCentre = getCentreOfRotation(oldGravity, newGravity, rotationParameters);
                Vec3d translation = RotationUtil.vecPlayerToWorld(relativeRotationCentre, oldGravity).subtract(RotationUtil.vecPlayerToWorld(relativeRotationCentre, newGravity));
                Direction relativeDirection = RotationUtil.dirWorldToPlayer(newGravity, oldGravity);
                Vec3d smidge = new Vec3d(
                        relativeDirection == Direction.EAST ? -1.0E-6D : 0.0D,
                        relativeDirection == Direction.UP ? -1.0E-6D : 0.0D,
                        relativeDirection == Direction.SOUTH ? -1.0E-6D : 0.0D
                );
                smidge = RotationUtil.vecPlayerToWorld(smidge, oldGravity);
                entity.setPosition(entity.getPos().add(translation).add(smidge));
                if(shouldChangeVelocity() && !rotationParameters.alternateCenter()) {
                    //Adjust entity position to avoid suffocation and collision
                    adjustEntityPosition(oldGravity, newGravity);
                }
            }
            if(shouldChangeVelocity()) {
                Vec3d realWorldVelocity = getRealWorldVelocity(entity, prevGravityDirection);
                if (rotationParameters.rotateVelocity()) {
                    //Rotate velocity with gravity, this will cause things to appear to take a sharp turn
                    Vector3f worldSpaceVec = new Vector3f((float)realWorldVelocity.x,(float)realWorldVelocity.y,(float)realWorldVelocity.z);
                    worldSpaceVec.rotate(RotationUtil.getRotationBetween(prevGravityDirection, gravityDirection));
                    entity.setVelocity(RotationUtil.vecWorldToPlayer(new Vec3d(worldSpaceVec), gravityDirection));
                } else {
                    //Velocity will be conserved relative to the world, will result in more natural motion
                    entity.setVelocity(RotationUtil.vecWorldToPlayer(realWorldVelocity, gravityDirection));
                }
            }
        }
        if(!entity.getWorld().isClient()) {
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }
    
    // getVelocity() does not return the actual velocity. It returns the velocity plus acceleration.
    // Even if the entity is standing still, getVelocity() will still give a downwards vector.
    // The real velocity is this tick position subtract last tick position
    private Vec3d getRealWorldVelocity(Entity entity, Direction prevGravityDirection) {
        if (entity.isLogicalSideForUpdatingMovement()) {
            return new Vec3d(
                entity.getX() - entity.prevX,
                entity.getY() - entity.prevY,
                entity.getZ() - entity.prevZ
            );
        }
        
        return RotationUtil.vecPlayerToWorld(entity.getVelocity(), prevGravityDirection);
    }
    
    private boolean shouldChangeVelocity() {
        if(entity instanceof FishingBobberEntity) return true;
        if(entity instanceof FireworkRocketEntity) return true;
        return !(entity instanceof ProjectileEntity);
    }

    @NotNull
    private Vec3d getCentreOfRotation(Direction oldGravity, Direction newGravity, RotationParameters rotationParameters) {
        Vec3d relativeRotationCentre = Vec3d.ZERO;
        if(entity instanceof EndCrystalEntity){
            //In the middle of the block below
            relativeRotationCentre = new Vec3d(0, -0.5, 0);
        }else if(rotationParameters.alternateCenter()) {
            EntityDimensions dimensions = entity.getDimensions(entity.getPose());
            if(newGravity.getOpposite() == oldGravity){
                //In the center of the hit-box
                relativeRotationCentre = new Vec3d(0, dimensions.height / 2, 0);
            }else {
                //Around the ankles
                relativeRotationCentre = new Vec3d(0, dimensions.width / 2, 0);
            }
        }
        return relativeRotationCentre;
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
        
        Iterable<VoxelShape> collisions = entity.getWorld().getCollisions(entity, entityBoundingBox);
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
    public double getGravityStrength() {
        double strength = 1;
        Gravity highestPriority = getHighestPriority();
        if (highestPriority != null) {
            strength = highestPriority.strength();
        }
        return defaultGravityStrength * GravityChangerAPI.getDimensionGravityStrength(entity.getWorld()) * strength;
    }

    @Override
    public double getDefaultGravityStrength() {
        return defaultGravityStrength;
    }

    @Override
    public void setDefaultGravityStrength(double strength) {
        if (canChangeGravity()) {
            defaultGravityStrength = strength;
        }
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
    public void updateGravity(RotationParameters rotationParameters, boolean initialGravity) {
        if (canChangeGravity()) {
            Direction newGravity = getActualGravityDirection();
            Direction oldGravity = gravityDirection;
            if (oldGravity != newGravity) {
                long timeMs = entity.getWorld().getTime() * 50;
                if(entity.getWorld().isClient) {
                    animation.applyRotationAnimation(
                            newGravity, oldGravity,
                            initialGravity ? 0 : rotationParameters.rotationTime(),
                            entity, timeMs, rotationParameters.rotateView()
                    );
                }
                gravityDirection = newGravity;
                prevGravityDirection = oldGravity;
                onGravityChanged(oldGravity, newGravity, rotationParameters, initialGravity);
            }
        }
    }
    
    @Override
    public Direction getActualGravityDirection() {
        Direction newGravity = getDefaultGravityDirection();
        Gravity highestPriority = getHighestPriority();
        if (highestPriority != null) {
            newGravity = highestPriority.direction();
        }
        if (isInverted) {
            newGravity = newGravity.getOpposite();
        }
        return newGravity;
    }

    @Nullable
    private Gravity getHighestPriority() {
        if(!gravityList.isEmpty()) {
            return Collections.max(gravityList, Comparator.comparingInt(Gravity::priority));
        }else{
            return null;
        }
    }

    @Override
    public void setDefaultGravityDirection(Direction gravityDirection, RotationParameters rotationParameters, boolean initialGravity) {
        if (canChangeGravity()) {
            defaultGravityDirection = gravityDirection;
            updateGravity(rotationParameters, initialGravity);
        }
    }
    
    @Override
    public void addGravity(Gravity gravity, boolean initialGravity) {
        if (canChangeGravity()) {
            gravityList.removeIf(g -> Objects.equals(g.source(), gravity.source()));
            if(gravity.direction() != null)
                gravityList.add(gravity);
            updateGravity(gravity.rotationParameters(), initialGravity);
        }
    }
    
    @Override
    public ArrayList<Gravity> getGravity() {
        return gravityList;
    }

    @Override
    public void setGravity(ArrayList<Gravity> _gravityList, boolean initialGravity) {
        Gravity highestBefore = getHighestPriority();
        gravityList = _gravityList;
        Gravity highestAfter = getHighestPriority();
        if(highestBefore != highestAfter){
            if(highestBefore == null){
                updateGravity(highestAfter.rotationParameters(), initialGravity);
            }else if(highestAfter == null){
                updateGravity(highestBefore.rotationParameters(), initialGravity);
            }else if(highestBefore.priority() > highestAfter.priority()){
                updateGravity(highestBefore.rotationParameters(), initialGravity);
            }else{
                updateGravity(highestAfter.rotationParameters(), initialGravity);
            }
        }
    }

    @Override
    public void invertGravity(boolean _isInverted, RotationParameters rotationParameters, boolean initialGravity) {
        isInverted = _isInverted;
        updateGravity(rotationParameters, initialGravity);
    }
    
    @Override
    public boolean getInvertGravity() {
        return this.isInverted;
    }

    @Override
    public void clearGravity(RotationParameters rotationParameters, boolean initialGravity) {
        gravityList.clear();
        updateGravity(rotationParameters, initialGravity);
    }
    
    @Override
    public RotationAnimation getGravityAnimation() {
        return animation;
    }
    
    @Override
    public void readFromNbt(NbtCompound nbt) {
        //Store old values
        Direction oldDefaultGravity = defaultGravityDirection;
        double oldDefaultStrength = defaultGravityStrength;
        ArrayList<Gravity> oldList = gravityList;
        boolean oldIsInverted = isInverted;
        //Load values from nbt
        if (nbt.contains("ListSize", NbtElement.INT_TYPE)) {
            int listSize = nbt.getInt("ListSize");
            ArrayList<Gravity> newGravityList = new ArrayList<>();
            if (listSize != 0) {
                for (int index = 0; index < listSize; index++) {
                    Gravity newGravity = new Gravity(
                        Direction.byId(nbt.getInt("GravityDirection " + index)),
                            nbt.getInt("GravityPriority " + index),
                            nbt.getDouble("GravityStrength " + index),
                        nbt.getInt("GravityDuration " + index),
                        nbt.getString("GravitySource " + index)
                    );
                    newGravityList.add(newGravity);
                }
            }
            gravityList = newGravityList;
        }
        prevGravityDirection = Direction.byId(nbt.getInt("PrevGravityDirection"));
        defaultGravityStrength = nbt.getDouble("DefaultGravityStrength");
        defaultGravityDirection = Direction.byId(nbt.getInt("DefaultGravityDirection"));
        isInverted = nbt.getBoolean("IsGravityInverted");
        //Update
        RotationParameters rp = new RotationParameters(false, false, false, 0);
        updateGravity(rp, true);
        //Check if an initial sync is required (actual sync happens in tick() because the network handler isn't initialised here yet)
        if (oldDefaultGravity != defaultGravityDirection) needsInitialSync = true;
        if (oldDefaultStrength != defaultGravityStrength) needsInitialSync = true;
        if (oldList.isEmpty() != gravityList.isEmpty()) needsInitialSync = true;
        if (oldIsInverted != isInverted) needsInitialSync = true;
    }
    
    @Override
    public void writeToNbt(@NotNull NbtCompound nbt) {
        int index = 0;
        for (Gravity temp : getGravity()) {
            if (temp.direction() != null && temp.source() != null) {
                nbt.putInt("GravityDirection " + index, temp.direction().getId());
                nbt.putInt("GravityPriority " + index, temp.priority());
                nbt.putDouble("GravityStrength " + index, temp.strength());
                nbt.putInt("GravityDuration " + index, temp.duration());
                nbt.putString("GravitySource " + index, temp.source());
                index++;
            }
        }
        nbt.putInt("ListSize", index);
        nbt.putInt("PrevGravityDirection", this.getPrevGravityDirection().getId());
        nbt.putDouble("DefaultGravityStrength", this.getDefaultGravityStrength());
        nbt.putInt("DefaultGravityDirection", this.getDefaultGravityDirection().getId());
        nbt.putBoolean("IsGravityInverted", this.getInvertGravity());
    }

    @Override
    public void tick(){
        Entity vehicle = entity.getVehicle();
        if (vehicle != null) {
            addGravity(new Gravity(GravityChangerAPI.getGravityDirection(vehicle), 99999999, 2, "vehicle"), true);
        }
        ArrayList<Gravity> gravityList = getGravity();
        Gravity highestBefore = getHighestPriority();
        if(gravityList.removeIf(g -> g.duration() == 0)){
            if (highestBefore != null) {
                updateGravity(highestBefore.rotationParameters(), false);
            }
        }
        for (Gravity temp : gravityList) {
            if (temp.duration() > 0) {
                temp.decrementDuration();
            }
        }
        if(!entity.getWorld().isClient && needsInitialSync){
            needsInitialSync = false;
            RotationParameters rotationParameters = new RotationParameters(false, false, false, 0);
            GravityChannel.sendFullStatePacket(entity, NetworkUtil.PacketMode.EVERYONE, rotationParameters, true);
        }
    }
}
