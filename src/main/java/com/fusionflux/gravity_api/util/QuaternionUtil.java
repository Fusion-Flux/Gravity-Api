package com.fusionflux.gravity_api.util;

import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public abstract class QuaternionUtil {
    public static float magnitude(Quaternion quaternion) {
        return MathHelper.sqrt(quaternion.getW() * quaternion.getW() + quaternion.getX() * quaternion.getX() + quaternion.getY() * quaternion.getY() + quaternion.getZ() * quaternion.getZ());
    }
    
    public static float magnitudeSq(Quaternion quaternion) {
        return quaternion.getW() * quaternion.getW() + quaternion.getX() * quaternion.getX() + quaternion.getY() * quaternion.getY() + quaternion.getZ() * quaternion.getZ();
    }
    
    public static Quaternion getViewRotation(float pitch, float yaw) {
        Quaternion r1 = new Quaternion(new Vec3f(1, 0, 0), pitch, true);
        Quaternion r2 = new Quaternion(new Vec3f(0, 1, 0), yaw + 180, true);
        r1.hamiltonProduct(r2);
        return r1;
    }
    
    // NOTE the "from" and "to" cannot be opposite
    public static Quaternion getRotationBetween(Vec3d from, Vec3d to) {
        from = from.normalize();
        to = to.normalize();
        Vec3d axis = from.crossProduct(to).normalize();
        double cos = from.dotProduct(to);
        double angle = Math.acos(cos);
        return new Quaternion(new Vec3f(axis), (float) angle, false);
    }
    
    // does not mutate the argument
    public static Quaternion mult(Quaternion a, Quaternion b) {
        Quaternion r = a.copy();
        r.hamiltonProduct(b);
        return r;
    }
    
    // does not mutate the argument
    public static Quaternion inversed(Quaternion a) {
        Quaternion r = a.copy();
        r.conjugate();
        return r;
    }
}
