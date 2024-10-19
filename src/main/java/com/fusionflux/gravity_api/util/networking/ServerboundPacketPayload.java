package com.fusionflux.gravity_api.util.networking;

import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.GravityPacketSender;
import com.fusionflux.gravity_api.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public non-sealed interface ServerboundPacketPayload extends BasePacketPayload {
	/**
	 * Called on the main client thread.
	 */
	void handle(ServerPlayer player, GravityComponent gc);

	static <T extends ServerboundPacketPayload> void handle(T packet, ServerPlayNetworking.Context ctx) {
		ServerPlayer player = ctx.player();
		ctx.server().execute(() -> 
			NetworkUtil.getGravityComponent(player).ifPresent(gc -> {
				packet.handle(player, gc);
				GravityPacketSender.sendToAllExceptSelf(ctx.server(), player, packet);
			})
		);
	}
}
