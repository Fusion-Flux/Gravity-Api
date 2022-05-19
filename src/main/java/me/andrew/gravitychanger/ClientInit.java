package me.andrew.gravitychanger;

import me.andrew.gravitychanger.api.GravityChangerAPI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.Direction;

public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(GravityChangerMod.CHANNEL_GRAVITY, (client, handler, buf, responseSender) -> {
            Direction gravityDirection = buf.readEnumConstant(Direction.class);
            boolean initialGravity = buf.readBoolean();
            client.execute(() -> {
                if(client.player == null) return;
                //GravityChangerAPI.setGravityDirection(client.player,gravityDirection);
               // ((RotatableEntityAccessor) client.player).gravitychanger$setGravityDirection(gravityDirection, initialGravity);
            });
        });
    }
}
