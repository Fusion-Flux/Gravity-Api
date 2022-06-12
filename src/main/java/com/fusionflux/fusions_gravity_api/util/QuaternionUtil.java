package com.fusionflux.fusions_gravity_api.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public abstract class QuaternionUtil {
    public static float magnitude(Quaternion quaternion) {
        return MathHelper.sqrt(quaternion.getW() * quaternion.getW() + quaternion.getX() * quaternion.getX() + quaternion.getY() * quaternion.getY() + quaternion.getZ() * quaternion.getZ());
    }

    public static float magnitudeSq(Quaternion quaternion) {
        return quaternion.getW() * quaternion.getW() + quaternion.getX() * quaternion.getX() + quaternion.getY() * quaternion.getY() + quaternion.getZ() * quaternion.getZ();
    }

    public static void inverse(Quaternion quaternion) {
        quaternion.conjugate();
        quaternion.scale(1.0F / magnitudeSq(quaternion));
    }
}
