package com.fusionflux.gravity_api.mixin.client;


import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemPickupParticle.class)
public abstract class ItemPickupParticleMixin {
    @Shadow @Final private Entity interactingEntity;

    @ModifyVariable(
            method = "buildGeometry",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getX()D",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            ordinal = 0
    )
    private double modify_buildGeometry_double_0(double value) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.interactingEntity);
        if(gravityDirection == Direction.DOWN) {
            return value;
        }

        return value + RotationUtil.vecPlayerToWorld(0.0D, 0.5D, 0.0D, gravityDirection).x;
    }

    @ModifyVariable(
            method = "buildGeometry",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getX()D",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            ordinal = 1
    )
    private double modify_buildGeometry_double_1(double value) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.interactingEntity);
        if(gravityDirection == Direction.DOWN) {
            return value;
        }

        return value - 0.5D + RotationUtil.vecPlayerToWorld(0.0D, 0.5D, 0.0D, gravityDirection).y;
    }

    @ModifyVariable(
            method = "buildGeometry",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getX()D",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            ordinal = 2
    )
    private double modify_buildGeometry_double_2(double value) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.interactingEntity);
        if(gravityDirection == Direction.DOWN) {
            return value;
        }

        return value + RotationUtil.vecPlayerToWorld(0.0D, 0.5D, 0.0D, gravityDirection).z;
    }
}
