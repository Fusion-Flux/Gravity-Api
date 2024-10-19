package com.fusionflux.gravity_api.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public class GravityPacketSender {
    @Environment(EnvType.CLIENT)
    public static void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToClients(Iterable<ServerPlayer> players, CustomPacketPayload payload) {
        for (ServerPlayer player : players) {
            sendToClient(player, payload);
        }
    }

    public static void sendToAllClients(MinecraftServer server, CustomPacketPayload payload) {
        Packet<?> packet = ServerPlayNetworking.createS2CPacket(payload);
        server.getPlayerList().broadcastAll(packet);
    }
    
    public static void sendToAllExceptSelf(MinecraftServer server, ServerPlayer self, CustomPacketPayload payload) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player != self) {
                sendToClient(player, payload);
            }
        }
    }

    public static void sendToClientsTrackingAndSelf(Entity entity, CustomPacketPayload payload) {
        Packet<?> packet = ServerPlayNetworking.createS2CPacket(payload);
        if (entity.level().getChunkSource() instanceof ServerChunkCache chunkCache) {
            chunkCache.broadcastAndSend(entity, packet);
        } else {
            throw new IllegalStateException("Cannot send clientbound payloads on the client");
        }
    }

    public static void sendToClientsTrackingEntity(Entity entity, CustomPacketPayload payload) {
        Packet<?> packet = ServerPlayNetworking.createS2CPacket(payload);
        if (entity.level().getChunkSource() instanceof ServerChunkCache chunkCache) {
            chunkCache.broadcast(entity, packet);
        } else {
            throw new IllegalStateException("Cannot send clientbound payloads on the client");
        }
    }

    public static void sendToClientsTrackingChunk(ServerLevel serverLevel, ChunkPos chunk, CustomPacketPayload payload) {
        for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(chunk, false)) {
            sendToClient(player, payload);
        }
    }

    public static void sendToClientsAround(ServerLevel serverLevel, Vec3 pos, double radius, CustomPacketPayload payload) {
        Packet<?> packet = ServerPlayNetworking.createS2CPacket(payload);
        serverLevel.getServer().getPlayerList().broadcast(null, pos.x(), pos.y(), pos.z(), radius, serverLevel.dimension(), packet);
    }

    public static void sendToClientsAround(ServerLevel serverLevel, Vec3i pos, double radius, CustomPacketPayload payload) {
        sendToClientsAround(serverLevel, new Vec3(pos.getX(), pos.getY(), pos.getZ()), radius, payload);
    }
}