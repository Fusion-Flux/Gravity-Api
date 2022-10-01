package com.fusionflux.gravity_api;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.fusionflux.gravity_api.config.GravityChangerConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> GravityChangerConfig.getScreen(parent,"gravity_api");
    }
}
