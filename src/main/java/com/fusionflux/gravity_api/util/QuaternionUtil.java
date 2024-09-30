package com.fusionflux.gravity_api.util;

import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public abstract class QuaternionUtil {
    public static float magnitude(Quaternionf quaternion) {
        return Mth.sqrt(quaternion.w() * quaternion.w() + quaternion.x() * quaternion.x() + quaternion.y() * quaternion.y() + quaternion.z() * quaternion.z());
    }
    
    public static float magnitudeSq(Quaternionf quaternion) {
        return quaternion.w() * quaternion.w() + quaternion.x() * quaternion.x() + quaternion.y() * quaternion.y() + quaternion.z() * quaternion.z();
    }
    
    public static Quaternionf getViewRotation(float pitch, float yaw) {
        Quaternionf r1 = new Quaternionf().fromAxisAngleDeg(new Vector3f(1.0F, 0.0F, 0.0F), pitch);
        Quaternionf r2 = new Quaternionf().fromAxisAngleDeg(new Vector3f(0.0F, 1.0F, 0.0F), yaw + 180);
        return CompatMath.hamiltonProduct(r1,r2);
    }
    
    // NOTE the "from" and "to" cannot be opposite
    public static Quaternionf getRotationBetween(Vector3d from, Vector3d to) {
        from = from.normalize();
        to = to.normalize();
        Vector3d axis = from.cross(to).normalize();
        double cos = from.dot(to);
        double angle = Math.acos(cos);
        return CompatMath.getQuat(new Vector3f((float)axis.x,(float)axis.y,(float)axis.z), (float) angle, false);
    }
    
    // does not mutate the argument
    public static Quaternionf mult(Quaternionf a, Quaternionf b) {
        return CompatMath.hamiltonProduct(a,b);
    }
    
    // does not mutate the argument
    public static Quaternionf inversed(Quaternionf a) {
        return a.conjugate();
    }
}
