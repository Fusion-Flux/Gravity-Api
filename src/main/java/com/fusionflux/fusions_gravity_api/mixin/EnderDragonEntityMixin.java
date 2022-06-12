package com.fusionflux.fusions_gravity_api.mixin;

import com.fusionflux.fusions_gravity_api.api.GravityChangerAPI;
import com.fusionflux.fusions_gravity_api.accessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends Entity implements EntityAccessor {


    public EnderDragonEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        Entity vehicle = this.getVehicle();
        if(vehicle != null) {
            return ((EntityAccessor) vehicle).gravitychanger$getAppliedGravityDirection();
        }

        return GravityChangerAPI.getGravityDirection((EnderDragonEntity)(Object)this);
    }

}
