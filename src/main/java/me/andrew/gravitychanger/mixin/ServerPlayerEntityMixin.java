package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(
            method = "worldChanged",
            at = @At("HEAD")
    )
    private void inject_worldChanged(CallbackInfo ci) {
        ((RotatableEntityAccessor) this).gravitychanger$setGravityDirection(Direction.DOWN);
    }
}
