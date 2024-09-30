package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class EntityTags {
    public static final TagKey<EntityType<?>> FORBIDDEN_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, GravityChangerMod.asResource("forbidden_entities"));
    public static final TagKey<EntityType<?>> FORBIDDEN_ENTITY_RENDERING = TagKey.create(Registries.ENTITY_TYPE, GravityChangerMod.asResource("forbidden_entity_rendering"));

    public static boolean canChangeGravity(Entity entity) {
            return !entity.getType().is(FORBIDDEN_ENTITIES);
    }
}
