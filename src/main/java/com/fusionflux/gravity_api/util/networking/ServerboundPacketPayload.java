package com.fusionflux.gravity_api.util.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public non-sealed interface ServerboundPacketPayload extends BasePacketPayload {
	/**
	 * Called on the main client thread.
	 */
	void handle(ServerPlayer player);

	static <T extends ServerboundPacketPayload> void handle(T packet, ServerPlayNetworking.Context ctx) {
		ctx.server().execute(() -> packet.handle(ctx.player()));
	}
}
