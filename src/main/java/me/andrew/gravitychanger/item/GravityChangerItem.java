package me.andrew.gravitychanger.item;

import me.andrew.gravitychanger.api.GravityChangerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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
       // if (!user.isSneaking()) {
       //     if (GravityChangerAPI.getGravityDirection(user) == this.gravityDirection) {
       //         GravityChangerAPI.setDefaultGravityDirection(user, this.gravityDirection);
       //     } else {
       //         GravityChangerAPI.setGravityDirection(user, this.gravityDirection);
       //     }
       // } else {
       //     GravityChangerAPI.setGravityDirection(user, GravityChangerAPI.getDefaultGravityDirection(user));
       // }
        Box box = user.getBoundingBox().expand(5);
        List<Entity> list = world.getEntitiesByClass(Entity.class, box, e -> !(e instanceof PlayerEntity && ((PlayerEntity) e).getAbilities().flying));
        for (Entity entity : list) {
            if(entity!=user)
            GravityChangerAPI.setGravityDirection(entity, this.gravityDirection);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {

        GravityChangerAPI.setGravityDirection(entity, this.gravityDirection);

        return ActionResult.PASS;

    }


}
