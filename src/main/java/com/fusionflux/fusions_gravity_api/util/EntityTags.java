package com.fusionflux.fusions_gravity_api.util;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityTags {
    public static final TagKey<EntityType<?>> FORBIDDEN_ENTITIES = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("gravitychanger", "forbidden_entities"));
    public static final TagKey<EntityType<?>> FORBIDDEN_ENTITY_RENDERING = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("gravitychanger", "forbidden_entity_rendering"));
}
