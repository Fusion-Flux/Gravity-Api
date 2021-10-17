package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.EntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemUsageContext.class)
public abstract class ItemUsageContextMixin {
    @Redirect(
            method = "getPlayerYaw",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private float redirect_getPlayerYaw_getYaw_0(PlayerEntity entity) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getYaw();
        }

        return RotationUtil.rotPlayerToWorld(entity.getYaw(), entity.getPitch(), gravityDirection).x;
    }
}
