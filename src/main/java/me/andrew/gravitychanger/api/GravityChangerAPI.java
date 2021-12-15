package me.andrew.gravitychanger.api;

import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public abstract class GravityChangerAPI {
    /**
     * Returns gravity direction for the given player
     */
    public static Direction getGravityDirection(PlayerEntity playerEntity) {
        return ((RotatableEntityAccessor) playerEntity).gravitychanger$getGravityDirection();
    }

    /**
     * Sets gravity direction for the given player
     * If the player is a ServerPlayerEntity and gravity direction changed also syncs the direction to the clients
     * If the player is either a ServerPlayerEntity or a ClientPlayerEntity also slightly adjusts player position
     */
    public static void setGravityDirection(PlayerEntity playerEntity, Direction gravityDirection) {
        ((RotatableEntityAccessor) playerEntity).gravitychanger$setGravityDirection(gravityDirection, false);
    }

    /**
     * Returns eye position offset from feet position for the given player
     */
    public static Vec3d getEyeOffset(PlayerEntity playerEntity) {
        return RotationUtil.vecPlayerToWorld(0, (double) playerEntity.getStandingEyeHeight(), 0, ((RotatableEntityAccessor) playerEntity).gravitychanger$getGravityDirection());
    }
}
