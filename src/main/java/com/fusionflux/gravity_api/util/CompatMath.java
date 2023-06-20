package com.fusionflux.gravity_api.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class CompatMath {
    public static Quaternionf getQuat(Vector3f axis, float rotationAngle, boolean degrees) {
        if (degrees) {
            rotationAngle *= 0.017453292F;
        }

        float f = sin(rotationAngle / 2.0F);
        return new Quaternionf(axis.x() * f,axis.y() * f,axis.z() * f,cos(rotationAngle / 2.0F));
    }

    public static Quaternionf hamiltonProduct(Quaternionf a,Quaternionf b) {
        float f = a.x();
        float g = a.y();
        float h = a.z();
        float i = a.w();
        float j = b.x();
        float k = b.y();
        float l = b.z();
        float m = b.w();
        float x = i * j + f * m + g * l - h * k;
        float y = i * k - f * l + g * m + h * j;
        float z = i * l + f * k - g * j + h * m;
        float w = i * m - f * j - g * k - h * l;
        return new Quaternionf(x,y,z,w);
    }

    public static BlockPos fastBlockPos(Vec3d p){
        return BlockPos.create(p.x,p.y,p.z);
    }
    private static float sin(float value) {
        return (float)Math.sin((double)value);
    }
    private static float cos(float value) {
        return (float)Math.cos((double)value);
    }

}
