package com.fusionflux.gravity_api.util.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
