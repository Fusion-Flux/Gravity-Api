package me.andrew.gravitychanger;

import me.andrew.gravitychanger.command.GravityCommand;
import me.andrew.gravitychanger.config.GravityChangerConfig;
import me.andrew.gravitychanger.item.ModItems;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.util.Identifier;

public class GravityChangerMod implements ModInitializer {
    public static final String MOD_ID = "gravitychanger";
    public static final Identifier CHANNEL_GRAVITY = new Identifier(MOD_ID, "gravity");

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
