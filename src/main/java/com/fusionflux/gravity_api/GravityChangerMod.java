package com.fusionflux.gravity_api;

import com.fusionflux.gravity_api.command.GravityCommand;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import com.fusionflux.gravity_api.util.GravityChannel;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GravityChangerMod implements ModInitializer {
    public static final String MOD_ID = "gravity_api";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static GravityChangerConfig config;

    //public static final ItemGroup GravityChangerGroup = FabricItemGroupBuilder.build(id("general"), () -> new ItemStack(ModItems.GRAVITY_CHANGER_UP));

    @Override
    public void onInitialize() {
        //ModItems.init();
        GravityChannel.initServer();

        MidnightConfig.init("gravity_api", GravityChangerConfig.class);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher));
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
