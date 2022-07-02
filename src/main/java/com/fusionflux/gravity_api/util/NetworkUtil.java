package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;


public class NetworkUtil {
    //Channels
    public static final Identifier CHANNEL_OVERWRITE_GRAVITY_LIST = GravityChangerMod.id("overwrite_gravity_list");
    public static final Identifier CHANNEL_UPDATE_GRAVITY_LIST = GravityChangerMod.id("update_gravity_list");
    public static final Identifier CHANNEL_DEFAULT_GRAVITY = GravityChangerMod.id("default_gravity");
    public static final Identifier CHANNEL_INVERTED = GravityChangerMod.id("inverted");

    public static GravityChannel<OverwriteGravityPacket> OVERWRITE_GRAVITY = new GravityChannel<>(OverwriteGravityPacket::new, CHANNEL_OVERWRITE_GRAVITY_LIST);
    public static GravityChannel<UpdateGravityPacket> UPDATE_GRAVITY = new GravityChannel<>(UpdateGravityPacket::new, CHANNEL_UPDATE_GRAVITY_LIST);
    public static GravityChannel<DefaultGravityPacket> DEFAULT_GRAVITY = new GravityChannel<>(DefaultGravityPacket::new, CHANNEL_DEFAULT_GRAVITY);
    public static GravityChannel<InvertGravityPacket> INVERT_GRAVITY = new GravityChannel<>(InvertGravityPacket::new, CHANNEL_INVERTED);

    //PacketMode
    public enum PacketMode{
        EVERYONE,
        EVERYONE_BUT_SELF,
        ONLY_SELF
    }

    //Access gravity component

    public static Optional<GravityComponent> getGravityComponent(MinecraftClient client, int entityId){
        if(client.world == null) return Optional.empty();
        Entity entity = client.world.getEntityById(entityId);
        if(entity == null) return Optional.empty();
        GravityComponent gc = GravityChangerAPI.getGravityComponent(entity);
        if(gc == null) return Optional.empty();
        return Optional.of(gc);
    }

    public static Optional<GravityComponent> getGravityComponent(ServerPlayerEntity player){
        GravityComponent gc = GravityChangerAPI.getGravityComponent(player);
        if(gc == null) return Optional.empty();
        return Optional.of(gc);
    }

    //Sending packets to players that are tracking an entity

    public static void sendToTracking(Entity entity, Identifier channel, PacketByteBuf buf, PacketMode mode){
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
                buf.readInt(),
                buf.readString(),
                readRotationParameters(buf)
        );
    }

    //Initialise Client and Server

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_DEFAULT_GRAVITY, DEFAULT_GRAVITY::receiveFromServer);
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_UPDATE_GRAVITY_LIST, UPDATE_GRAVITY::receiveFromServer);
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_OVERWRITE_GRAVITY_LIST, OVERWRITE_GRAVITY::receiveFromServer);
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_INVERTED, INVERT_GRAVITY::receiveFromServer);
    }

    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_DEFAULT_GRAVITY, DEFAULT_GRAVITY::receiveFromClient);
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_UPDATE_GRAVITY_LIST, UPDATE_GRAVITY::receiveFromClient);
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_OVERWRITE_GRAVITY_LIST, OVERWRITE_GRAVITY::receiveFromClient);
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_INVERTED, INVERT_GRAVITY::receiveFromClient);
    }
}