package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.config.GravityChangerConfig;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class GravityDimensionStrengthComponent implements GravityStrengthComponent {
    double gravityStrength = 1;

    @Override
    public double getDimensionGravityStrength() {
        return gravityStrength;
    }

    @Override
    public void setDimensionGravityStrength(double strength) {
        gravityStrength = strength;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        gravityStrength = tag.getDouble("DimensionGravityStrength");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putDouble("DimensionGravityStrength" , gravityStrength);
    }
}
