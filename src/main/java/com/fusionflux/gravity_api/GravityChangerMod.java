package com.fusionflux.gravity_api;

import com.fusionflux.gravity_api.command.GravityCommand;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import com.fusionflux.gravity_api.item.ModItems;
import com.fusionflux.gravity_api.item.Verifier;
import com.fusionflux.gravity_api.util.GravityChannel;
import com.fusionflux.gravity_api.util.GravityComponent;
import com.fusionflux.gravity_api.util.GravityPackets;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

public class GravityChangerMod implements ModInitializer {
    public static final String MOD_ID = "gravity_api";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static GravityChangerConfig config;

    public static final CreativeModeTab GravityChangerGroup = FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.GRAVITY_CHANGER_UP)).title(Component.translatable("itemGroup.gravity_api.general")).displayItems((params, collector) -> {
        collector.accept(ModItems.GRAVITY_CHANGER_DOWN);
        collector.accept(ModItems.GRAVITY_CHANGER_UP);
        collector.accept(ModItems.GRAVITY_CHANGER_NORTH);
        collector.accept(ModItems.GRAVITY_CHANGER_SOUTH);
        collector.accept(ModItems.GRAVITY_CHANGER_EAST);
        collector.accept(ModItems.GRAVITY_CHANGER_WEST);
        collector.accept(ModItems.GRAVITY_CHANGER_DOWN_AOE);
        collector.accept(ModItems.GRAVITY_CHANGER_UP_AOE);
        collector.accept(ModItems.GRAVITY_CHANGER_NORTH_AOE);
        collector.accept(ModItems.GRAVITY_CHANGER_SOUTH_AOE);
        collector.accept(ModItems.GRAVITY_CHANGER_EAST_AOE);
        collector.accept(ModItems.GRAVITY_CHANGER_WEST_AOE);
    }).build();

    public static final ComponentKey<GravityComponent> GRAVITY_COMPONENT =
            ComponentRegistry.getOrCreate(asResource("gravity_direction"), GravityComponent.class);
    @Override
    public void onInitialize() {
        GravityChannel.DEFAULT_GRAVITY.getVerifierRegistry().register(Verifier.FIELD_GRAVITY_SOURCE, Verifier::check);

        MidnightConfig.init("gravity_api", GravityChangerConfig.class);
        ModItems.init();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, asResource("general"), GravityChangerGroup);
        GravityPackets.registerPackets();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher));
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
