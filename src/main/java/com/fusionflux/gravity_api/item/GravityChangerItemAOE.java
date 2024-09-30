package com.fusionflux.gravity_api.item;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GravityChangerItemAOE extends Item {
    public final Direction gravityDirection;

    public GravityChangerItemAOE(Item.Properties settings, Direction _gravityDirection) {
        super(settings);
        gravityDirection = _gravityDirection;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(!level.isClientSide()) {
            AABB box = player.getBoundingBox().inflate(3);
            List<Entity> list = level.getEntitiesOfClass(Entity.class, box, e -> !(e instanceof Player));
            for (Entity entity : list) {
                GravityChangerAPI.setDefaultGravityDirection(entity, gravityDirection, new RotationParameters());
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
