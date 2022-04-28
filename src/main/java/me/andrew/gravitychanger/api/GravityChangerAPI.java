package me.andrew.gravitychanger.api;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.util.GravityComponent;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public abstract class GravityChangerAPI {
    public static final ComponentKey<GravityComponent> GRAVITY_COMPONENT =
            ComponentRegistry.getOrCreate(new Identifier("gravitychanger", "gravity_direction"), GravityComponent.class);
    /**
     * Returns the applied gravity direction for the given player
     * This is the direction that directly affects everything this mod changes
     * If the player is riding a vehicle this will be the applied gravity direction of the vehicle
     * Otherwise it will be the main gravity gravity direction of the player itself
     */
    public static Direction getAppliedGravityDirection(PlayerEntity playerEntity) {
        return ((EntityAccessor) playerEntity).gravitychanger$getAppliedGravityDirection();
    }

    /**
     * Returns the main gravity direction for the given player
     * This may not be the applied gravity direction for the player, see GravityChangerAPI#getAppliedGravityDirection
     */
    public static Direction getGravityDirection(Entity playerEntity) {
        //if(playerEntity instanceof RotatableEntityAccessor)
        //if(playerEntity != null)
        return GRAVITY_COMPONENT.maybeGet(playerEntity).map(GravityComponent::getTrackedGravityDirection).orElse(Direction.DOWN);
       // return ((RotatableEntityAccessor) playerEntity).gravitychanger$getGravityDirection();
        //return  Direction.DOWN;
    }

    public static Direction getDefaultGravityDirection(Entity playerEntity) {
        //if(playerEntity instanceof RotatableEntityAccessor)
       //if(playerEntity != null)
        return GRAVITY_COMPONENT.maybeGet(playerEntity).map(GravityComponent::getDefaultTrackedGravityDirection).orElse(Direction.DOWN);
        //return ((RotatableEntityAccessor) playerEntity).gravitychanger$getDefaultGravityDirection();
        //return  Direction.DOWN;
    }


    /**
     * Sets the main gravity direction for the given player
     * If the player is a ServerPlayerEntity and gravity direction changed also syncs the direction to the clients
     * If the player is either a ServerPlayerEntity or a ClientPlayerEntity also slightly adjusts player position
     * This may not immediately change the applied gravity direction for the player, see GravityChangerAPI#getAppliedGravityDirection
     */
    public static void setGravityDirection(Entity playerEntity, Direction gravityDirection) {
        //if(playerEntity instanceof RotatableEntityAccessor)
        GRAVITY_COMPONENT.maybeGet(playerEntity).ifPresent(gc -> gc.setTrackedGravityDirection(gravityDirection));
        //((RotatableEntityAccessor) playerEntity).gravitychanger$setGravityDirection(gravityDirection, false);
    }


    public static void setDefaultGravityDirection(Entity playerEntity, Direction gravityDirection) {
        //if(playerEntity instanceof RotatableEntityAccessor)
        GRAVITY_COMPONENT.maybeGet(playerEntity).ifPresent(gc -> gc.setDefaultTrackedGravityDirection(gravityDirection));
        //((RotatableEntityAccessor) playerEntity).gravitychanger$setDefaultGravityDirection(gravityDirection, false);
    }

    /**
     * Returns the world relative velocity for the given player
     * Using minecraft's methods to get the velocity of a the player will return player relative velocity
     */
    public static Vec3d getWorldVelocity(PlayerEntity playerEntity) {
        return RotationUtil.vecPlayerToWorld(playerEntity.getVelocity(), ((EntityAccessor) playerEntity).gravitychanger$getAppliedGravityDirection());
    }

    /**
     * Sets the world relative velocity for the given player
     * Using minecraft's methods to set the velocity of a the player will set player relative velocity
     */
    public static void setWorldVelocity(PlayerEntity playerEntity, Vec3d worldVelocity) {
        playerEntity.setVelocity(RotationUtil.vecWorldToPlayer(worldVelocity, ((EntityAccessor) playerEntity).gravitychanger$getAppliedGravityDirection()));
    }

    /**
     * Returns eye position offset from feet position for the given player
     */
    public static Vec3d getEyeOffset(PlayerEntity playerEntity) {
        return RotationUtil.vecPlayerToWorld(0, (double) playerEntity.getStandingEyeHeight(), 0, ((EntityAccessor) playerEntity).gravitychanger$getAppliedGravityDirection());
    }
}
