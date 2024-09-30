package com.fusionflux.gravity_api;

import com.fusionflux.gravity_api.util.GravityChannel;
import net.fabricmc.api.ClientModInitializer;

public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GravityChannel.initClient();
    }
}
