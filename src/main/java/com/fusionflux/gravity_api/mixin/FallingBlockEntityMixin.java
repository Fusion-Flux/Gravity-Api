package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    //@ModifyReturnValue(method = "fall", at = @At("RETURN"))
    //private static FallingBlockEntity applyGravityF(FallingBlockEntity entity, @Local BlockPos pos) {
    //    final Direction gravity = GeneralUtil.getGravityForBlockPos((ServerWorld)entity.world, pos);
    //    GravityChangerAPI.addGravity(entity, new Gravity(gravity, 5, 2, "star_heart"));
    //    if (gravity != Direction.DOWN) {
    //        entity.velocityDirty = true;
    //    }
    //    return entity;
    //}
//
    //@Inject(method = "tick", at = @At("HEAD"))
    //private void applyGravityT(CallbackInfo ci) {
    //    GeneralUtil.setAppropriateEntityGravity(this);
    //    if (GravityChangerAPI.getGravityDirection(this) != Direction.DOWN) {
    //        velocityDirty = true;
    //    }
    //}
//
    //@Redirect(
    //    method = "tick",
    //    at = @At(
    //        value = "INVOKE",
    //        target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
    //    )
    //)
    //private BlockPos relativeToGravity(BlockPos instance) {
    //    return instance.offset(GeneralUtil.getGravityForBlockPos((ServerWorld)world, instance));
    //}
//
    //@Override
    //protected Box calculateBoundingBox() {
    //    final Box original = super.calculateBoundingBox();
    //    final Direction gravity = GravityChangerAPI.getGravityDirection(this);
    //    if (gravity == Direction.DOWN) {
    //        return original;
    //    }
    //    return original.offset(gravity.getOffsetX() * 0.5, 0.5, gravity.getOffsetZ() * 0.5);
    //}

    @ModifyArg(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"
        ),
        index = 1
    )
    private double multiplyGravity(double x) {
        return x * GravityChangerAPI.getGravityStrength(this);
    }
}
