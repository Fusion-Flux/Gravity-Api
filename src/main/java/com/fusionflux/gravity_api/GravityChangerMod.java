package com.fusionflux.gravity_api;

import com.fusionflux.gravity_api.command.GravityCommand;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import com.fusionflux.gravity_api.item.ModItems;
import com.fusionflux.gravity_api.util.GravityChannel;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GravityChangerMod implements ModInitializer {
    public static final String MOD_ID = "gravity_api";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static GravityChangerConfig config;

    public static final ItemGroup GravityChangerGroup = FabricItemGroupBuilder.build(id("general"), () -> new ItemStack(ModItems.GRAVITY_CHANGER_UP));

    @Override
    public void onInitialize() {
        ModItems.init();
        GravityChannel.initServer();

        AutoConfig.register(GravityChangerConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(GravityChangerConfig.class).getConfig();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher));
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
