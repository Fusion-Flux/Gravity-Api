package com.fusionflux.gravity_api.util;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class GravityStrengthWorldComponent implements WorldComponentInitializer {

    public static final ComponentKey<GravityStrengthComponent> GRAVITY_MODIFIER =
            ComponentRegistry.getOrCreate(new Identifier("gravityapi", "gravity_strength"), GravityStrengthComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(World.class, GRAVITY_MODIFIER, GravityDimensionStrengthComponent::new);
    }
}
