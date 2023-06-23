package com.fusionflux.gravity_api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class EntityTags {
    public static final TagKey<EntityType<?>> FORBIDDEN_ENTITIES = TagKey.of(Registries.ENTITY_TYPE.getKey(), new Identifier("gravity_api", "forbidden_entities"));
    public static final TagKey<EntityType<?>> FORBIDDEN_ENTITY_RENDERING = TagKey.of(Registries.ENTITY_TYPE.getKey(), new Identifier("gravity_api", "forbidden_entity_rendering"));

    public static boolean canChangeGravity(Entity entity) {
            return !entity.getType().isIn(FORBIDDEN_ENTITIES);
    }
}
