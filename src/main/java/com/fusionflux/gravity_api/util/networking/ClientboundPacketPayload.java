package com.fusionflux.gravity_api.util.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;

public non-sealed interface ClientboundPacketPayload extends BasePacketPayload {
	/**
	 * Called on the main client thread.
	 * Make sure that implementations are also annotated, or else servers may crash.
	 */
	@Environment(EnvType.CLIENT)
	void handle(LocalPlayer player);

	@Environment(EnvType.CLIENT)
	static <T extends ClientboundPacketPayload> void handle(T packet, ClientPlayNetworking.Context ctx) {
		ctx.client().execute(() -> packet.handle(ctx.player()));
	}
}
