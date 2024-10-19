package com.fusionflux.gravity_api.util.networking;

import com.fusionflux.gravity_api.util.GravityComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public sealed interface BasePacketPayload extends CustomPacketPayload permits ClientboundPacketPayload, ServerboundPacketPayload {
	PacketTypeProvider getTypeProvider();
	
	@Override
	@ApiStatus.NonExtendable
	default @NotNull Type<? extends CustomPacketPayload> type() {
		return this.getTypeProvider().getType();
	}

	interface PacketTypeProvider {
		<T extends CustomPacketPayload> Type<T> getType();
	}
}
