package me.andrew.gravitychanger;

import me.andrew.gravitychanger.item.ModItems;
import net.fabricmc.api.ModInitializer;

public class GravityChangerMod implements ModInitializer {
    public static final String MOD_ID = "gravitychanger";

    @Override
    public void onInitialize() {
        ModItems.init();
    }
}
