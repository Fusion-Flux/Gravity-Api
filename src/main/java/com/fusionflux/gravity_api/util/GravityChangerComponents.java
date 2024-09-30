package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import net.minecraft.world.entity.Entity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class GravityChangerComponents implements EntityComponentInitializer {

    public static final ComponentKey<GravityComponent> GRAVITY_MODIFIER =
            ComponentRegistry.getOrCreate(GravityChangerMod.asResource("gravity_direction"), GravityComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, GRAVITY_MODIFIER, GravityDirectionComponent::new);
        registry.registerForPlayers(GRAVITY_MODIFIER, GravityDirectionComponent::new, GravityChangerConfig.resetGravityOnRespawn ? RespawnCopyStrategy.LOSSLESS_ONLY : RespawnCopyStrategy.CHARACTER);
    }
}
