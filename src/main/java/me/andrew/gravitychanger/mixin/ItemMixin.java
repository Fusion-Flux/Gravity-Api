package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Redirect(
            method = "raycast",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private static Vec3d redirect_raycast_add_0(Vec3d vec3d, double x, double y, double z, World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) player;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        return vec3d.add(RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection));
    }
}
