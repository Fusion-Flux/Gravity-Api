package com.fusionflux.gravity_api.api;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.util.*;
import com.fusionflux.gravity_api.util.packet.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentProvider;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

public abstract class GravityChangerAPI {
    public static final ComponentKey<GravityComponent> GRAVITY_COMPONENT =
            ComponentRegistry.getOrCreate(GravityChangerMod.asResource("gravity_direction"), GravityComponent.class);

    public static final ComponentKey<GravityDimensionStrengthInterface> GRAVITY_DIMENSION_STRENGTH_COMPONENT =
            ComponentRegistry.getOrCreate(GravityChangerMod.asResource("gravity_dimension_strength"), GravityDimensionStrengthInterface.class);
    
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

    public static double getDefaultGravityStrength(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getDefaultGravityStrength).orElse(1d);
        }
        return 1;
    }



    public static double getGravityStrength(Entity entity) {
        if (EntityTags.canChangeGravity(entity)) {
            return maybeGetSafe(GRAVITY_COMPONENT, entity).map(GravityComponent::getGravityStrength).orElse(1d);
        }
        return 1d;
    }

    public static double getDimensionGravityStrength(Level level) {
        return maybeGetSafe(GRAVITY_DIMENSION_STRENGTH_COMPONENT, level).map(GravityDimensionStrengthInterface::getDimensionGravityStrength).orElse(1d);
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
     * If the player is either a ServerPlayerEntity or a LocalPlayer also slightly adjusts player position
     * This may not immediately change the applied gravity direction for the player, see GravityChangerAPI#getAppliedGravityDirection
     */
    public static void addGravity(Entity entity, Gravity gravity) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.addGravity(gravity, false);
                    GravityChannel.UPDATE_GRAVITY.sendToClient(entity, new UpdateGravityPacket(gravity, false), NetworkUtil.PacketMode.EVERYONE);
                });
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static void addGravityClient(LocalPlayer entity, Gravity gravity, ResourceLocation verifier, FriendlyByteBuf verifierInfo) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.addGravity(gravity, false);
                    GravityChannel.UPDATE_GRAVITY.sendToServer(new UpdateGravityPacket(gravity, false), verifier, verifierInfo);
                });
            }
        }
    }

    /**
    * Update gravity should always be automatically called when you call any api function
    * that could result in a gravityDirection change.
    * */
    public static void updateGravity(Entity entity) {
        updateGravity(entity, new RotationParameters());
    }

    public static void updateGravity(Entity entity, RotationParameters rotationParameters) {
        if (EntityTags.canChangeGravity(entity)) {
            maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> gc.updateGravity(rotationParameters,false));
        }
    }

    public static void setGravity(Entity entity, ArrayList<Gravity> gravity) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setGravity(gravity, false);
                    GravityChannel.OVERWRITE_GRAVITY.sendToClient(entity, new OverwriteGravityPacket(gravity, false), NetworkUtil.PacketMode.EVERYONE);
                });
            }
        }
    }
    @Environment(EnvType.CLIENT)
    public static void setGravityClient(LocalPlayer entity, ArrayList<Gravity> gravity, ResourceLocation verifier, FriendlyByteBuf verifierInfo) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setGravity(gravity, false);
                    GravityChannel.OVERWRITE_GRAVITY.sendToServer(new OverwriteGravityPacket(gravity, false), verifier, verifierInfo);
                });
            }
        }
    }

    public static void setIsInverted(Entity entity, boolean isInverted){
        setIsInverted(entity, isInverted, new RotationParameters());
    }

    public static void setIsInverted(Entity entity, boolean isInverted, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.invertGravity(isInverted, rotationParameters, false);
                    GravityChannel.INVERT_GRAVITY.sendToClient(entity, new InvertGravityPacket(isInverted, rotationParameters, false), NetworkUtil.PacketMode.EVERYONE);
                });
            }
        }
    }

    public static void setDefualtGravityStrength(Entity entity, double strength) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setDefaultGravityStrength(strength);
                    GravityChannel.DEFAULT_GRAVITY_STRENGTH.sendToClient(entity, new DefaultGravityStrengthPacket(strength), NetworkUtil.PacketMode.EVERYONE);
                });
            }
        }
    }

    public static void setDimensionGravityStrength(Level level, double strength) {
        maybeGetSafe(GRAVITY_DIMENSION_STRENGTH_COMPONENT, level).ifPresent(component -> component.setDimensionGravityStrength(strength));
    }

    @Environment(EnvType.CLIENT)
    public static void setIsInvertedClient(LocalPlayer entity, boolean isInverted, RotationParameters rotationParameters, ResourceLocation verifier, FriendlyByteBuf verifierInfo) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.invertGravity(isInverted, rotationParameters, false);
                    GravityChannel.INVERT_GRAVITY.sendToServer(new InvertGravityPacket(isInverted, rotationParameters, false), verifier, verifierInfo);
                });
            }
        }
    }

    public static void clearGravity(Entity entity){
        clearGravity(entity, new RotationParameters());
    }

    public static void clearGravity(Entity entity, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.clearGravity(rotationParameters, false);
                    GravityChannel.OVERWRITE_GRAVITY.sendToClient(entity, new OverwriteGravityPacket( new ArrayList<>(), false), NetworkUtil.PacketMode.EVERYONE);
                });
            }
        }
    }
    @Environment(EnvType.CLIENT)
    public static void clearGravityClient(LocalPlayer entity, RotationParameters rotationParameters, ResourceLocation verifier, FriendlyByteBuf verifierInfo) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.clearGravity(rotationParameters, false);
                    GravityChannel.OVERWRITE_GRAVITY.sendToServer(new OverwriteGravityPacket(new ArrayList<>(), false), verifier, verifierInfo);
                });
            }
        }
    }

    @Deprecated
    public static void setDefaultGravityDirection(Entity entity, Direction gravityDirection, int animationDurationMs) {
        setDefaultGravityDirection(entity, gravityDirection, new RotationParameters().rotationTime(animationDurationMs));
    }

    public static void setDefaultGravityDirection(Entity entity, Direction gravityDirection) {
        setDefaultGravityDirection(entity, gravityDirection, new RotationParameters());
    }

    public static void setDefaultGravityDirection(Entity entity, Direction gravityDirection, RotationParameters rotationParameters) {
        if(onCorrectSide(entity, true)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setDefaultGravityDirection(gravityDirection, rotationParameters, false);
                    GravityChannel.DEFAULT_GRAVITY.sendToClient(entity, new DefaultGravityPacket(entity.getId(), gravityDirection, rotationParameters, false), NetworkUtil.PacketMode.EVERYONE);
                });
            }
        }
    }
    @Environment(EnvType.CLIENT)
    public static void setDefaultGravityDirectionClient(LocalPlayer entity, Direction gravityDirection, RotationParameters rotationParameters, ResourceLocation verifier, FriendlyByteBuf verifierInfo) {
        if(onCorrectSide(entity, false)) {
            if (EntityTags.canChangeGravity(entity)) {
                maybeGetSafe(GRAVITY_COMPONENT, entity).ifPresent(gc -> {
                    gc.setDefaultGravityDirection(gravityDirection, rotationParameters, false);
                    GravityChannel.DEFAULT_GRAVITY.sendToServer(new DefaultGravityPacket(gravityDirection, rotationParameters, false), verifier, verifierInfo);
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
    public static Vec3 getWorldVelocity(Entity playerEntity) {
        return RotationUtil.vecPlayerToWorld(playerEntity.getDeltaMovement(), getGravityDirection(playerEntity));
    }

    /**
     * Sets the world relative velocity for the given player
     * Using minecraft's methods to set the velocity of an entity will set player relative velocity
     */
    public static void setWorldVelocity(Entity entity, Vec3 worldVelocity) {
        entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(worldVelocity, getGravityDirection(entity)));
    }

    /**
     * Returns eye position offset from feet position for the given entity
     */
    public static Vec3 getEyeOffset(Entity entity) {
        return RotationUtil.vecPlayerToWorld(0, (double) entity.getEyeHeight(), 0, getGravityDirection(entity));
    }

    private static boolean onCorrectSide(Entity entity, boolean shouldBeOnServer){
        if(entity.level().isClientSide() && shouldBeOnServer) {
            GravityChangerMod.LOGGER.error("GravityChangerAPI function cannot be called from the server, use dedicated client server. ", new Exception());
            return false;
        }
        if(!entity.level().isClientSide() && !shouldBeOnServer) {
            GravityChangerMod.LOGGER.error("GravityChangerAPI function cannot be called from the client, use dedicated client client. ", new Exception());
            return false;
        }
        return true;
    }
}
