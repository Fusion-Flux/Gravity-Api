package com.fusionflux.fusions_gravity_api.item;

import com.fusionflux.fusions_gravity_api.api.GravityChangerAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GravityChangerItem extends Item {
    public final Direction gravityDirection;

    public GravityChangerItem(Settings settings, Direction gravityDirection) {
        super(settings);

        this.gravityDirection = gravityDirection;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient)
            GravityChangerAPI.setDefaultGravityDirection(user, this.gravityDirection);
            //GravityChangerAPI.updateGravity(user);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

   // @Override
   // public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
   //     if(!(entity instanceof PlayerEntity))
   //     if (!user.isSneaking()) {
   //         if (GravityChangerAPI.getGravityDirection(entity) == this.gravityDirection) {
   //             GravityChangerAPI.setDefaultGravityDirection(entity, this.gravityDirection);
   //         } else {
   //             GravityChangerAPI.setGravityDirection(entity, this.gravityDirection);
   //         }
   //     } else {
   //         GravityChangerAPI.setGravityDirection(entity, GravityChangerAPI.getDefaultGravityDirection(entity));
   //     }
   //     return ActionResult.PASS;
   // }

}
