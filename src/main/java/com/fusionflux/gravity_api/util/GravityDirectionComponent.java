package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
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
        entity.setPos(entity.position());//Causes bounding box recalculation
        
        if (!initialGravity) {
            if(!(entity instanceof ServerPlayer)) {
                //A relativeRotationCentre of zero will result in zero translation
                Vec3 relativeRotationCentre = getCentreOfRotation(oldGravity, newGravity, rotationParameters);
                Vec3 translation = RotationUtil.vecPlayerToWorld(relativeRotationCentre, oldGravity).subtract(RotationUtil.vecPlayerToWorld(relativeRotationCentre, newGravity));
                Direction relativeDirection = RotationUtil.dirWorldToPlayer(newGravity, oldGravity);
                Vec3 smidge = new Vec3(
                        relativeDirection == Direction.EAST ? -1.0E-6D : 0.0D,
                        relativeDirection == Direction.UP ? -1.0E-6D : 0.0D,
                        relativeDirection == Direction.SOUTH ? -1.0E-6D : 0.0D
                );
                smidge = RotationUtil.vecPlayerToWorld(smidge, oldGravity);
                entity.setPos(entity.position().add(translation).add(smidge));
                if(shouldChangeVelocity() && !rotationParameters.alternateCenter()) {
                    //Adjust entity position to avoid suffocation and collision
                    adjustEntityPosition(oldGravity, newGravity);
                }
            }
            if(shouldChangeVelocity()) {
                Vec3 realWorldVelocity = getRealWorldVelocity(entity, prevGravityDirection);
                if (rotationParameters.rotateVelocity()) {
                    //Rotate velocity with gravity, this will cause things to appear to take a sharp turn
                    Vector3f worldSpaceVec = new Vector3f((float)realWorldVelocity.x,(float)realWorldVelocity.y,(float)realWorldVelocity.z);
                    worldSpaceVec.rotate(RotationUtil.getRotationBetween(prevGravityDirection, gravityDirection));
                    entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(new Vec3(worldSpaceVec), gravityDirection));
                } else {
                    //Velocity will be conserved relative to the world, will result in more natural motion
                    entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(realWorldVelocity, gravityDirection));
                }
            }
        }
        if (!entity.level().isClientSide()) {
            GravityChangerComponents.GRAVITY_MODIFIER.sync(entity);
        }
    }
    
    // getVelocity() does not return the actual velocity. It returns the velocity plus acceleration.
    // Even if the entity is standing still, getVelocity() will still give a downwards vector.
    // The real velocity is this tick position subtract last tick position
    private Vec3 getRealWorldVelocity(Entity entity, Direction prevGravityDirection) {
        if (entity.isControlledByLocalInstance()) {
            return new Vec3(
                entity.getX() - entity.xo,
                entity.getY() - entity.yo,
                entity.getZ() - entity.zo
            );
        }
        
        return RotationUtil.vecPlayerToWorld(entity.getDeltaMovement(), prevGravityDirection);
    }
    
    private boolean shouldChangeVelocity() {
        if(entity instanceof FishingHook) return true;
        if(entity instanceof FireworkRocketEntity) return true;
        return !(entity instanceof Projectile);
    }

    @NotNull
    private Vec3 getCentreOfRotation(Direction oldGravity, Direction newGravity, RotationParameters rotationParameters) {
        Vec3 relativeRotationCentre = Vec3.ZERO;
        if (entity instanceof EndCrystal) {
            //In the middle of the block below
            relativeRotationCentre = new Vec3(0, -0.5, 0);
        } else if (rotationParameters.alternateCenter()) {
            EntityDimensions dimensions = entity.getDimensions(entity.getPose());
            if(newGravity.getOpposite() == oldGravity){
                //In the center of the hit-box
                relativeRotationCentre = new Vec3(0, dimensions.height() / 2, 0);
            }else {
                //Around the ankles
                relativeRotationCentre = new Vec3(0, dimensions.width() / 2, 0);
            }
        }
        return relativeRotationCentre;
    }

    // Adjust position to avoid suffocation in blocks when changing gravity
    private void adjustEntityPosition(Direction oldGravity, Direction newGravity) {
        if (entity instanceof AreaEffectCloud
                || entity instanceof PersistentProjectileEntity
                || entity instanceof EndCrystal) {
            return;
        }
        
        AABB entityBoundingBox = entity.getBoundingBox();
        
        // for example, if gravity changed from down to north, move up
        // if gravity changed from down to up, also move up
        Direction movingDirection = oldGravity.getOpposite();
        
        Iterable<VoxelShape> collisions = entity.level().getCollisions(entity, entityBoundingBox);
        AABB totalCollisionBox = null;
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
    public void readFromNbt(CompoundTag nbt, HolderLookup.Provider registries) {
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
    public void writeToNbt(@NotNull CompoundTag nbt, HolderLookup.Provider registries) {
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
