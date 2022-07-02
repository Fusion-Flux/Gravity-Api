package com.fusionflux.gravity_api.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

import static com.fusionflux.gravity_api.util.NetworkUtil.*;

public class GravityChannel<P extends GravityPacket> {
    private final P gravityPacket;
    private final Identifier channel;

    GravityChannel(P _gravityPacket, Identifier _channel){
        gravityPacket = _gravityPacket;
        channel = _channel;
    }

    public void sendToClient(Entity entity, P packet){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        packet.write(buf);
        sendToTracking(entity, channel, buf);
    }

    public void receiveFromServer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int entityId = buf.readInt();
        GravityPacket packet = gravityPacket.read(buf);
        client.execute(() -> {
            getGravityComponent(client, entityId).ifPresent(packet::run);
        });
    }

    public void sendToServer(P packet){
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        ClientPlayNetworking.send(CHANNEL_OVERWRITE_GRAVITY_LIST, buf);
    }

    public void receiveFromClient(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int listSize = buf.readInt();
        GravityPacket packet = gravityPacket.read(buf);
        server.execute(() -> {
            getGravityComponent(player).ifPresent(packet::run);
        });
    }
}
