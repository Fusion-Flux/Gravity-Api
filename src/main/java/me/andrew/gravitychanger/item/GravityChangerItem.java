package me.andrew.gravitychanger.item;

import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.Gravity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

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
