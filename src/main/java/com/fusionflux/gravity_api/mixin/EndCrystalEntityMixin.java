package com.fusionflux.gravity_api.mixin;

import com.fusionflux.gravity_api.accessor.EntityAccessor;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalEntityMixin extends Entity implements EntityAccessor {

    //private static final TrackedData<Direction> gravitychanger$GRAVITY_DIRECTION = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.FACING);
//
    //private static final TrackedData<Direction> gravitychanger$DEFAULT_GRAVITY_DIRECTION = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.FACING);
//
    //private Direction gravitychanger$prevGravityDirection = Direction.DOWN;

    public EndCrystalEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        Entity vehicle = this.getVehicle();
        if(vehicle != null) {
            return ((EntityAccessor) vehicle).gravitychanger$getAppliedGravityDirection();
        }

        return GravityChangerAPI.getGravityDirection((EndCrystalEntity)(Object)this);
    }

    }


