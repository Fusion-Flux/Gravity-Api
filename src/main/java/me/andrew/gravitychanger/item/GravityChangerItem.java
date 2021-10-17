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
        if(!world.isClient) {
            ((RotatableEntityAccessor) user).gravitychanger$setGravityDirection(this.gravityDirection);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
