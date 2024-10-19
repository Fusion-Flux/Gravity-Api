package com.fusionflux.gravity_api.util.networking;

import com.fusionflux.gravity_api.util.GravityComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface ClientboundAndServerboundPacketPayload extends ClientboundPacketPayload, ServerboundPacketPayload {
    @Override
    default void handle(LocalPlayer player, GravityComponent gc) {
        handleBoth(player, gc);
    }

    @Override
    default void handle(ServerPlayer player, GravityComponent gc) {
        handleBoth(player, gc);
    }
    
    void handleBoth(Player player, GravityComponent gc);
}
