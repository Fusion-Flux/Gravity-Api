package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;


public class NetworkUtil {
    //PacketMode
    public enum PacketMode{
        EVERYONE,
        EVERYONE_BUT_SELF,
        ONLY_SELF
    }

    //Access gravity component

    public static Optional<GravityComponent> getGravityComponent(Level level, int entityId) {
        if (level == null)
            return Optional.empty();
        
        Entity entity = level.getEntity(entityId);
        if (entity == null)
            return Optional.empty();
        
        return getGravityComponent(entity);
    }

    public static Optional<GravityComponent> getGravityComponent(Entity entity){
        GravityComponent gc = GravityChangerAPI.getGravityComponent(entity);
        if (gc == null)
            return Optional.empty();
        return Optional.of(gc);
    }

    //Sending packets to players that are tracking an entity

    public static void sendToTracking(Entity entity, ResourceLocation channel, PacketByteBuf buf, PacketMode mode){
        //PlayerLookup.tracking(entity) might not return the player if entity is a player, so it has to be done separately
        if(mode != PacketMode.EVERYONE_BUT_SELF)
            if(entity instanceof ServerPlayerEntity player)
                ServerPlayNetworking.send(player, channel, buf);
        if(mode != PacketMode.ONLY_SELF)
            for (ServerPlayerEntity player : PlayerLookup.tracking(entity))
                if(player != entity)
                    ServerPlayNetworking.send(player, channel, buf);
    }

    //Writing to buffer

    public static void writeDirection(PacketByteBuf buf, Direction direction){
        buf.writeByte(direction == null ? -1 : direction.getId());
    }

    public static void writeRotationParameters(PacketByteBuf buf, RotationParameters rotationParameters){
        buf.writeBoolean(rotationParameters.rotateVelocity());
        buf.writeBoolean(rotationParameters.rotateView());
        buf.writeBoolean(rotationParameters.alternateCenter());
        buf.writeInt(rotationParameters.rotationTime());
    }

    public static void writeGravity(PacketByteBuf buf, Gravity gravity){
        writeDirection(buf, gravity.direction());
        buf.writeInt(gravity.priority());
        buf.writeDouble(gravity.strength());
        buf.writeInt(gravity.duration());
        buf.writeString(gravity.source());
        writeRotationParameters(buf, gravity.rotationParameters());
    }

    //Reading from buffer

    public static RotationParameters readRotationParameters(PacketByteBuf buf){
        return new RotationParameters(
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readInt()
        );
    }

    public static Direction readDirection(PacketByteBuf buf){
        int rawDirection = buf.readByte();
        return (0 <= rawDirection && rawDirection < Direction.values().length) ? Direction.byId(rawDirection) : null;
    }

    public static Gravity readGravity(PacketByteBuf buf){
        return new Gravity(
                readDirection(buf),
                buf.readInt(),
                buf.readDouble(),
                buf.readInt(),
                buf.readString(),
                readRotationParameters(buf)
        );
    }
}