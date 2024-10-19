package com.fusionflux.gravity_api.util.networking;

import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.NetworkUtil;
import com.fusionflux.gravity_api.util.packet.GravityPacket;
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
	void handle(LocalPlayer player, GravityComponent gc);

	@Environment(EnvType.CLIENT)
	static <T extends ClientboundPacketPayload> void handle(T packet, ClientPlayNetworking.Context ctx) {
		ctx.client().execute(() ->
			NetworkUtil.getGravityComponent(ctx.client().level, ((GravityPacket) packet).entityId).ifPresent(gc -> {
				packet.handle(ctx.player(), gc);
			})
		);
	}
}
