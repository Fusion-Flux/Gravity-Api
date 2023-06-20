package com.fusionflux.gravity_api.config;

import com.fusionflux.gravity_api.config.GravityChangerConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

@SuppressWarnings("unused")
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> GravityChangerConfig.getScreen(parent, "gravity_api");
    }
}
