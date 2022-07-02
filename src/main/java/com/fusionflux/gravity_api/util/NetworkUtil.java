package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.api.Gravity;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.GravityVerifier;
import com.fusionflux.gravity_api.api.RotationParameters;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Optional;


public class NetworkUtil {
    //Channels
    public static final Identifier CHANNEL_OVERWRITE_GRAVITY_LIST = GravityChangerMod.id("overwrite_gravity_list");
    public static final Identifier CHANNEL_UPDATE_GRAVITY_LIST = GravityChangerMod.id("update_gravity_list");
    public static final Identifier CHANNEL_DEFAULT_GRAVITY = GravityChangerMod.id("default_gravity");
    public static final Identifier CHANNEL_INVERTED = GravityChangerMod.id("inverted");

    //Access gravity component

    private static Optional<GravityComponent> getGravityComponent(MinecraftClient client, int entityId){
        if(client.world == null) return Optional.empty();
        Entity entity = client.world.getEntityById(entityId);
        if(entity == null) return Optional.empty();
        GravityComponent gc = GravityChangerAPI.getGravityComponent(entity);
        if(gc == null) return Optional.empty();
        return Optional.of(gc);
    }

    private static Optional<GravityComponent> getGravityComponent(ServerPlayerEntity player){
        GravityComponent gc = GravityChangerAPI.getGravityComponent(player);
        if(gc == null) return Optional.empty();
        return Optional.of(gc);
    }

    //Sending packets to players that are tracking an entity

    private static void sendToTracking(Entity entity, Identifier channel, PacketByteBuf buf){
        //PlayerLookup.tracking(entity) might not return the player if entity is a player, so it has to be done separately
        if(entity instanceof ServerPlayerEntity player)
            ServerPlayNetworking.send(player, channel, buf);
        sendToTrackingExcludingSelf(entity, channel, buf);
    }

    private static void sendToTrackingExcludingSelf(Entity entity, Identifier channel, PacketByteBuf buf){
        for (ServerPlayerEntity player : PlayerLookup.tracking(entity))
            if(player != entity)
                ServerPlayNetworking.send(player, channel, buf);
    }

    //Writing to buffer

    public static void write(PacketByteBuf buf, Direction direction){
        buf.writeByte(direction == null ? -1 : direction.getId());
    }

    public static void write(PacketByteBuf buf, RotationParameters rotationParameters){
        buf.writeBoolean(rotationParameters.rotateVelocity());
        buf.writeBoolean(rotationParameters.rotateView());
        buf.writeBoolean(rotationParameters.alternateCenter());
        buf.writeInt(rotationParameters.rotationTime());
    }

    public static void write(PacketByteBuf buf, Gravity gravity){
        write(buf, gravity.direction());
        buf.writeInt(gravity.priority());
        buf.writeInt(gravity.duration());
        buf.writeString(gravity.source());
        write(buf, gravity.rotationParameters());
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

    //Gravity List: Overwrite

    public static void sendOverwriteGravityListToClient(Entity entity, ArrayList<Gravity> gravityList, boolean initialGravity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        buf.writeInt(gravityList.size());
        for (Gravity gravity : gravityList) write(buf, gravity);
        buf.writeBoolean(initialGravity);
        sendToTracking(entity, CHANNEL_OVERWRITE_GRAVITY_LIST, buf);
    }

    public static void receiveOverwriteGravityListFromServer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int entityId = buf.readInt();
        int listSize = buf.readInt();
        ArrayList<Gravity> gravityList = new ArrayList<>();
        for (int i = 0; i < listSize; i++) gravityList.add(readGravity(buf));
        boolean initialGravity = buf.readBoolean();
        client.execute(() -> {
            getGravityComponent(client, entityId).ifPresent(gc -> gc.setGravity(gravityList, initialGravity));
        });
    }

    public static void sendOverwriteGravityListToServer(ArrayList<Gravity> gravityList, boolean initialGravity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(gravityList.size());
        for (Gravity gravity : gravityList) write(buf, gravity);
        buf.writeBoolean(initialGravity);
        ClientPlayNetworking.send(CHANNEL_OVERWRITE_GRAVITY_LIST, buf);
    }

    public static void receiveOverwriteGravityListFromClient(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int listSize = buf.readInt();
        ArrayList<Gravity> gravityList = new ArrayList<>();
        for (int i = 0; i < listSize; i++) gravityList.add(readGravity(buf));
        boolean initialGravity = buf.readBoolean();
        server.execute(() -> {
            getGravityComponent(player).ifPresent(gc -> gc.setGravity(gravityList, initialGravity));
        });
    }

    //Gravity List: Update

    public static void sendUpdateGravityListToClient(Entity entity, Gravity gravity, boolean initialGravity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        write(buf, gravity);
        buf.writeBoolean(initialGravity);
        sendToTracking(entity, CHANNEL_UPDATE_GRAVITY_LIST, buf);
    }

    public static void receiveUpdateGravityListFromServer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int entityId = buf.readInt();
        Gravity gravity = readGravity(buf);
        boolean initialGravity = buf.readBoolean();
        client.execute(() -> {
            getGravityComponent(client, entityId).ifPresent(gc -> gc.addGravity(gravity, initialGravity));
        });
    }

    public static void sendUpdateGravityListToServer(Gravity gravity, boolean initialGravity){
        PacketByteBuf buf = PacketByteBufs.create();
        write(buf, gravity);
        buf.writeBoolean(initialGravity);
        ClientPlayNetworking.send(CHANNEL_UPDATE_GRAVITY_LIST, buf);
    }

    public static void receiveUpdateGravityListFromClient(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        Gravity gravity = readGravity(buf);
        boolean initialGravity = buf.readBoolean();
        server.execute(() -> {
            getGravityComponent(player).ifPresent(gc -> gc.addGravity(gravity, initialGravity));
        });
    }

    //Default Gravity

    public static void sendDefaultGravityToClient(Entity entity, Direction direction, RotationParameters rotationParameters, boolean initialGravity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        write(buf, direction);
        write(buf, rotationParameters);
        buf.writeBoolean(initialGravity);
        sendToTracking(entity, CHANNEL_DEFAULT_GRAVITY, buf);
    }

    public static void receiveDefaultGravityFromServer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int entityId = buf.readInt();
        Direction direction = readDirection(buf);
        RotationParameters rotationParameters = readRotationParameters(buf);
        boolean initialGravity = buf.readBoolean();
        client.execute(() -> {
            getGravityComponent(client, entityId).ifPresent(gc -> gc.setDefaultGravityDirection(direction, rotationParameters, initialGravity));
        });
    }

    public static void sendDefaultGravityToServer(Direction direction, RotationParameters rotationParameters, boolean initialGravity){
        PacketByteBuf buf = PacketByteBufs.create();
        write(buf, direction);
        write(buf, rotationParameters);
        buf.writeBoolean(initialGravity);
        ClientPlayNetworking.send(CHANNEL_DEFAULT_GRAVITY, buf);
    }

    public static void receiveDefaultGravityFromClient(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        Direction direction = readDirection(buf);
        RotationParameters rotationParameters = readRotationParameters(buf);
        boolean initialGravity = buf.readBoolean();
        server.execute(() -> {
            getGravityComponent(player).ifPresent(gc -> {
                GravityVerifier.SetDefaultGravityVerifier test = GravityVerifier.getForSetDefaultGravity(new Identifier(GravityChangerMod.MOD_ID, "test"));
                if(test == null) return;
                if(!test.check(player, PacketByteBufs.create(), direction)) return;
                gc.setDefaultGravityDirection(direction, rotationParameters, initialGravity);
            });
        });
    }

    //Inverted

    public static void sendInvertedToClient(Entity entity, boolean inverted, RotationParameters rotationParameters, boolean initialGravity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        buf.writeBoolean(inverted);
        write(buf, rotationParameters);
        buf.writeBoolean(initialGravity);
        sendToTracking(entity, CHANNEL_INVERTED, buf);
    }

    public static void receiveInvertedFromServer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int entityId = buf.readInt();
        boolean inverted = buf.readBoolean();
        RotationParameters rotationParameters = readRotationParameters(buf);
        boolean initialGravity = buf.readBoolean();
        client.execute(() -> {
            getGravityComponent(client, entityId).ifPresent(gc -> gc.invertGravity(inverted, rotationParameters, initialGravity));
        });
    }

    public static void sendInvertedToServer(boolean inverted, RotationParameters rotationParameters, boolean initialGravity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(inverted);
        write(buf, rotationParameters);
        buf.writeBoolean(initialGravity);
        ClientPlayNetworking.send(CHANNEL_INVERTED, buf);
    }

    public static void receiveInvertedFromClient(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        boolean inverted = buf.readBoolean();
        RotationParameters rotationParameters = readRotationParameters(buf);
        boolean initialGravity = buf.readBoolean();
        server.execute(() -> {
            getGravityComponent(player).ifPresent(gc -> gc.invertGravity(inverted, rotationParameters, initialGravity));
        });
    }

    //Initialise Client and Server

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_DEFAULT_GRAVITY, NetworkUtil::receiveDefaultGravityFromServer);
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_UPDATE_GRAVITY_LIST, NetworkUtil::receiveUpdateGravityListFromServer);
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_OVERWRITE_GRAVITY_LIST, NetworkUtil::receiveOverwriteGravityListFromServer);
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_INVERTED, NetworkUtil::receiveInvertedFromServer);
    }

    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_DEFAULT_GRAVITY, NetworkUtil::receiveDefaultGravityFromClient);
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_UPDATE_GRAVITY_LIST, NetworkUtil::receiveUpdateGravityListFromClient);
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_OVERWRITE_GRAVITY_LIST, NetworkUtil::receiveOverwriteGravityListFromClient);
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_INVERTED, NetworkUtil::receiveInvertedFromClient);
    }
}