package me.andrew.gravitychanger;

import me.andrew.gravitychanger.command.GravityCommand;
import me.andrew.gravitychanger.config.GravityChangerConfig;
import me.andrew.gravitychanger.item.GravityChangerItem;
import me.andrew.gravitychanger.item.ModItems;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class GravityChangerMod implements ModInitializer {
    public static final String MOD_ID = "gravitychanger";
    public static final Identifier CHANNEL_GRAVITY = new Identifier(MOD_ID, "gravity");

    public static final ItemGroup GravityChangerGroup = FabricItemGroupBuilder.build(
            id("general"),
            () -> new ItemStack(ModItems.GRAVITY_CHANGER_UP));

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static GravityChangerConfig config;

    @Override
    public void onInitialize() {
        ModItems.init();

        AutoConfig.register(GravityChangerConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(GravityChangerConfig.class).getConfig();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            GravityCommand.register(dispatcher);
        });
    }
}
