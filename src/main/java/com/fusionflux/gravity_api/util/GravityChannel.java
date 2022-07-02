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

import static com.fusionflux.gravity_api.util.NetworkUtil.*;

public class GravityChannel<P extends GravityPacket> {
    private final Factory<P> packetFactory;
    private final Identifier channel;

    GravityChannel(Factory<P> _packetFactory, Identifier _channel){
        packetFactory = _packetFactory;
        channel = _channel;
    }

    public void sendToClient(Entity entity, P packet, PacketMode mode){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        packet.write(buf);
        sendToTracking(entity, channel, buf, mode);
    }

    public void receiveFromServer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int entityId = buf.readInt();
        GravityPacket packet = packetFactory.read(buf);
        client.execute(() -> {
            getGravityComponent(client, entityId).ifPresent(packet::run);
        });
    }

    public void sendToServer(P packet, Identifier verifier, PacketByteBuf verifierInfoBuf){
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        buf.writeIdentifier(verifier);
        buf.writeByteArray(verifierInfoBuf.array());
        ClientPlayNetworking.send(channel, buf);
    }

    public void receiveFromClient(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int listSize = buf.readInt();
        GravityPacket packet = packetFactory.read(buf);
        Identifier verifier = buf.readIdentifier();
        PacketByteBuf verifierInfoBuf = PacketByteBufs.create().writeByteArray(buf.readByteArray());
        server.execute(() -> {
            getGravityComponent(player).ifPresent(packet::run);
        });
    }

    @FunctionalInterface
    interface Factory<T extends GravityPacket> {
        T read(PacketByteBuf buf);
    }
}
