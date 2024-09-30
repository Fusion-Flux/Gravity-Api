package com.fusionflux.gravity_api.item;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GravityChangerItem extends Item {
    public final Direction gravityDirection;

    public GravityChangerItem(Item.Properties settings, Direction _gravityDirection) {
        super(settings);
        gravityDirection = _gravityDirection;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) {
            if (!player.isShiftKeyDown()) {
                GravityChangerAPI.setDefaultGravityDirectionClient((LocalPlayer) player, gravityDirection, new RotationParameters(), Verifier.FIELD_GRAVITY_SOURCE, Verifier.packInfo(player.blockPosition()));
            } else {
                GravityChangerAPI.setDefaultGravityDirectionClient((LocalPlayer) player, Direction.DOWN, new RotationParameters(), Verifier.FIELD_GRAVITY_SOURCE, Verifier.packInfo(player.blockPosition()));
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(interactionHand));
    }
}
