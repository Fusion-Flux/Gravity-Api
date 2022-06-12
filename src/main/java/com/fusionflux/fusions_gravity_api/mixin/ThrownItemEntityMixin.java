package com.fusionflux.fusions_gravity_api.mixin;

import com.fusionflux.fusions_gravity_api.accessor.EntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ThrownItemEntity.class)
public abstract class ThrownItemEntityMixin extends ThrownEntity implements EntityAccessor {




    protected ThrownItemEntityMixin(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

   // @Override
   // public Direction gravitychanger$getAppliedGravityDirection() {
   //     Entity vehicle = this.getVehicle();
   //     if(vehicle != null) {
   //         GravityChangerAPI.setGravityDirection((ThrownItemEntity)(Object)this,GravityChangerAPI.getGravityDirection(vehicle));
   //         return ((EntityAccessor) vehicle).gravitychanger$getAppliedGravityDirection();
   //     }
//
   //     return GravityChangerAPI.getGravityDirection((ThrownItemEntity)(Object)this);
   // }








}
