package me.andrew.gravitychanger.item;

import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
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
        if(!user.isSneaking()) {
            if(((RotatableEntityAccessor) user).gravitychanger$getGravityDirection() == this.gravityDirection) {
                ((RotatableEntityAccessor) user).gravitychanger$setDefaultGravityDirection(this.gravityDirection, false);
            }else {
                ((RotatableEntityAccessor) user).gravitychanger$setGravityDirection(this.gravityDirection, false);
            }
            }else{
            ((RotatableEntityAccessor) user).gravitychanger$setGravityDirection(((RotatableEntityAccessor) user).gravitychanger$getDefaultGravityDirection(), false);
        }


        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
