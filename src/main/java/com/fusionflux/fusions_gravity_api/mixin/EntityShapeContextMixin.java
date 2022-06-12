package com.fusionflux.fusions_gravity_api.mixin;

import com.fusionflux.fusions_gravity_api.accessor.EntityAccessor;
import com.fusionflux.fusions_gravity_api.util.RotationUtil;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityShapeContext.class)
public abstract class EntityShapeContextMixin {
    @Shadow @Final private Entity entity;

    @Shadow @Final private double minY;

    @Redirect(
            method = "<init>(Lnet/minecraft/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getY()D",
                    ordinal = 0
            )
    )
    private static double redirect_init_getY_0(Entity entity) {
        Direction gravityDirection = ((EntityAccessor) entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) {
            return entity.getY();
        }

        return RotationUtil.boxWorldToPlayer(entity.getBoundingBox(), gravityDirection).minY;
    }

    @Inject(
            method = "isAbove",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue, CallbackInfoReturnable<Boolean> cir) {
        if(this.entity == null) return;

        Direction gravityDirection = ((EntityAccessor) this.entity).gravitychanger$getAppliedGravityDirection();
        if(gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(this.minY > RotationUtil.boxWorldToPlayer(new Box(pos), gravityDirection).minY + RotationUtil.boxWorldToPlayer(shape.getBoundingBox().expand(-9.999999747378752E-6D), gravityDirection).maxX);
    }
}
