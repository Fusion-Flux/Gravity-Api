package com.fusionflux.gravity_api;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.command.GravityCommand;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import com.fusionflux.gravity_api.item.GravityChangerItem;
import com.fusionflux.gravity_api.item.ModItems;
import com.fusionflux.gravity_api.util.GravityChannel;
import com.fusionflux.gravity_api.util.GravityComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.PlayerCopyCallback;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GravityChangerMod implements ModInitializer {
    public static final String MOD_ID = "gravity_api";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static GravityChangerConfig config;

    public static final ItemGroup GravityChangerGroup = FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.GRAVITY_CHANGER_UP)).name(Text.translatable("itemGroup.gravity_api.general")).entries((params, collector) -> {
        collector.addItem(ModItems.GRAVITY_CHANGER_DOWN);
        collector.addItem(ModItems.GRAVITY_CHANGER_UP);
        collector.addItem(ModItems.GRAVITY_CHANGER_NORTH);
        collector.addItem(ModItems.GRAVITY_CHANGER_SOUTH);
        collector.addItem(ModItems.GRAVITY_CHANGER_EAST);
        collector.addItem(ModItems.GRAVITY_CHANGER_WEST);
        collector.addItem(ModItems.GRAVITY_CHANGER_DOWN_AOE);
        collector.addItem(ModItems.GRAVITY_CHANGER_UP_AOE);
        collector.addItem(ModItems.GRAVITY_CHANGER_NORTH_AOE);
        collector.addItem(ModItems.GRAVITY_CHANGER_SOUTH_AOE);
        collector.addItem(ModItems.GRAVITY_CHANGER_EAST_AOE);
        collector.addItem(ModItems.GRAVITY_CHANGER_WEST_AOE);
    }).build();

    public static final ComponentKey<GravityComponent> GRAVITY_COMPONENT =
            ComponentRegistry.getOrCreate(new Identifier("gravityapi", "gravity_direction"), GravityComponent.class);
    @Override
    public void onInitialize() {
        MidnightConfig.init("gravity_api", GravityChangerConfig.class);
        ModItems.init();
        Registry.register(Registries.ITEM_GROUP, new Identifier("gravity_api", "general"), GravityChangerGroup);
        GravityChannel.initServer();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher));
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
