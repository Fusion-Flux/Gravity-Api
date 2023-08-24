package com.fusionflux.gravity_api.item;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GravityChangerItem extends Item {
    public final Direction gravityDirection;

    public GravityChangerItem(Settings settings, Direction _gravityDirection) {
        super(settings);
        gravityDirection = _gravityDirection;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            if (!user.isSneaking()) {
                GravityChangerAPI.setDefaultGravityDirectionClient((ClientPlayerEntity) user, gravityDirection, new RotationParameters(), Verifier.FIELD_GRAVITY_SOURCE, Verifier.packInfo(user.getBlockPos()));
            }else{
                GravityChangerAPI.setDefaultGravityDirectionClient((ClientPlayerEntity) user, Direction.DOWN, new RotationParameters(), Verifier.FIELD_GRAVITY_SOURCE, Verifier.packInfo(user.getBlockPos()));
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
