package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class GravityDimensionStrengthWorldRegister implements WorldComponentInitializer {

    public static final ComponentKey<GravityDimensionStrengthInterface> GRAVITY_DIMENSION_STRENGTH_MODIFIER =
            ComponentRegistry.getOrCreate(GravityChangerMod.asResource("gravity_dimension_strength"), GravityDimensionStrengthInterface.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(GRAVITY_DIMENSION_STRENGTH_MODIFIER, GravityDimensionStrengthComponent::new);
    }
}
