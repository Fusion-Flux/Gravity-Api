package com.fusionflux.gravity_api.api;

import java.util.ArrayList;
import java.util.Optional;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.util.*;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public abstract class GravityChangerAPI {
    public static final ComponentKey<GravityComponent> GRAVITY_COMPONENT =
            ComponentRegistry.getOrCreate(new Identifier("gravityapi", "gravity_direction"), GravityComponent.class);
    
    // workaround for a CCA bug; maybeGet throws an NPE in internal code if the DataTracker isn't initialized
    // null check the component container to avoid it
    private static <C extends Component, V> Optional<C> maybeGetSafe(ComponentKey<C> key, @Nullable V provider) {
        if (provider instanceof ComponentProvider p) {
            var cc = p.getComponentContainer();
            if (cc != null) {
                return Optional.ofNullable(key.getInternal(cc));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the applied gravity direction for the given player
     * This is the direction that directly affects everything this mod changes
     * If the player is riding a vehicle this will be the applied gravity direction of the vehicle
     * Otherwise it will be the main gravity gravity direction of the player itself
     */
    public static Direction getGravityDirection(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getGravityDirection).orElse(Direction.DOWN);
        }
        return Direction.DOWN;
    }

    public static ArrayList<Gravity> getGravityList(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getGravity).orElse(new ArrayList<Gravity>());
        }
        return new ArrayList<Gravity>();
    }

    public static Direction getPrevGravtityDirection(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getPrevGravityDirection).orElse(Direction.DOWN);
        }
        return Direction.DOWN;
    }

    /**
     * Returns the main gravity direction for the given player
     * This may not be the applied gravity direction for the player, see GravityChangerAPI#getAppliedGravityDirection
     */
    public static Direction getDefaultGravityDirection(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getDefaultGravityDirection).orElse(Direction.DOWN);
        }
        return Direction.DOWN;
    }

    public static Direction getActualGravityDirection(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getActualGravityDirection).orElse(Direction.DOWN);
        }
        return Direction.DOWN;
    }

    public static boolean getIsInverted(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getInvertGravity).orElse(false);
        }
        return false;
    }

    public static Optional<RotationAnimation> getGravityAnimation(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getGravityAnimation);
        }
        return Optional.empty();
    }

    /**
     * Sets the main gravity direction for the given player
     * If the player is a ServerPlayerEntity and gravity direction changed also syncs the direction to the clients
     * If the player is either a ServerPlayerEntity or a ClientPlayerEntity also slightly adjusts player position
     * This may not immediately change the applied gravity direction for the player, see GravityChangerAPI#getAppliedGravityDirection
     */
    public static void addGravity(Entity entity, Gravity gravity) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.addGravity(gravity, false);
                    NetworkUtil.sendUpdateGravityListToClient(entity, gravity, false);
                });
            }
        }
    }

    public static void addGravityClient(ClientPlayerEntity entity, Gravity gravity) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.addGravity(gravity, false);
                    NetworkUtil.sendUpdateGravityListToServer(gravity, false);
                });
            }
        }
    }

    public static void setGravity(Entity entity, ArrayList<Gravity> gravity) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setGravity(gravity, false);
                    NetworkUtil.sendOverwriteGravityListToClient(entity, gravity, false);
                });
            }
        }
    }

    public static void setGravityClient(ClientPlayerEntity entity, ArrayList<Gravity> gravity) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setGravity(gravity, false);
                    NetworkUtil.sendOverwriteGravityListToServer(gravity, false);
                });
            }
        }
    }

    public static void setIsInverted(Entity entity, boolean isInverted, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.invertGravity(isInverted, rotationParameters, false);
                    NetworkUtil.sendInvertedToClient(entity, isInverted);
                });
            }
        }
    }

    public static void setIsInvertedClient(ClientPlayerEntity entity, boolean isInverted, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.invertGravity(isInverted, rotationParameters, false);
                    NetworkUtil.sendInvertedToServer(isInverted);
                });
            }
        }
    }

    public static void clearGravity(Entity entity, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.clearGravity(rotationParameters, false);
                    NetworkUtil.sendOverwriteGravityListToClient(entity, new ArrayList<>(), false);
                });
            }
        }
    }

    public static void clearGravityClient(ClientPlayerEntity entity, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.clearGravity(rotationParameters, false);
                    NetworkUtil.sendOverwriteGravityListToServer(new ArrayList<>(), false);
                });
            }
        }
    }

    public static void setDefaultGravityDirection(Entity entity, Direction gravityDirection, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setDefaultGravityDirection(gravityDirection, rotationParameters, false);
                    NetworkUtil.sendDefaultGravityToClient(entity, gravityDirection, rotationParameters);
                });
            }
        }
    }

    public static void setDefaultGravityDirectionClient(ClientPlayerEntity entity, Direction gravityDirection, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setDefaultGravityDirection(gravityDirection, rotationParameters, false);
                    NetworkUtil.sendDefaultGravityToServer(gravityDirection, rotationParameters);
                });
            }
        }
    }

    /**
     * For internal use only, direct calls on GravityComponent will cause de-sync between client and server.
     */
    @Nullable
    public static GravityComponent getGravityComponent(Entity entity){
        return maybeGetSafe(GRAVITY_COMPONENT, entity).orElse(null);
    }
    
    /**
     * Returns the world relative velocity for the given player
     * Using minecraft's methods to get the velocity of a the player will return player relative velocity
     */
    public static Vec3d getWorldVelocity(Entity playerEntity) {
        return RotationUtil.vecPlayerToWorld(playerEntity.getVelocity(), getGravityDirection(playerEntity));
    }

    /**
     * Sets the world relative velocity for the given player
     * Using minecraft's methods to set the velocity of an entity will set player relative velocity
     */
    public static void setWorldVelocity(Entity entity, Vec3d worldVelocity) {
        entity.setVelocity(RotationUtil.vecWorldToPlayer(worldVelocity, getGravityDirection(entity)));
    }

    /**
     * Returns eye position offset from feet position for the given entity
     */
    public static Vec3d getEyeOffset(Entity entity) {
        return RotationUtil.vecPlayerToWorld(0, (double) entity.getStandingEyeHeight(), 0, getGravityDirection(entity));
    }

    private static boolean onCorrectSide(Entity entity, boolean shouldBeOnServer){
        if(entity.world.isClient && shouldBeOnServer) {
            GravityChangerMod.LOGGER.error("GravityChangerAPI function cannot be called from the server, use dedicated client server. ", new Exception());
            return false;
        }
        if(!entity.world.isClient && !shouldBeOnServer) {
            GravityChangerMod.LOGGER.error("GravityChangerAPI function cannot be called from the client, use dedicated client client. ", new Exception());
            return false;
        }
        return true;
    }
}
