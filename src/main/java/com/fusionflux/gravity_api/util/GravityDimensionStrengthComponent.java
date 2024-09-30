package com.fusionflux.gravity_api.util;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class GravityDimensionStrengthComponent implements GravityDimensionStrengthInterface {
    double gravityStrength = 1;

    private final Level currentLevel;

    public GravityDimensionStrengthComponent(Level level) {
        this.currentLevel = level;
    }

    @Override
    public double getDimensionGravityStrength() {
        return gravityStrength;
    }

    @Override
    public void setDimensionGravityStrength(double strength) {
        if (!currentLevel.isClientSide()) {
            gravityStrength = strength;
            GravityDimensionStrengthWorldRegister.GRAVITY_DIMENSION_STRENGTH_MODIFIER.sync(currentLevel);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registries) {
        gravityStrength = tag.getDouble("DimensionGravityStrength");
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putDouble("DimensionGravityStrength" , gravityStrength);
    }
}
