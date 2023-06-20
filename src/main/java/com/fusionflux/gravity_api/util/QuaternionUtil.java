package com.fusionflux.gravity_api.util;

import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class QuaternionUtil {
    public static float magnitude(Quaternionf quaternion) {
        return MathHelper.sqrt(quaternion.w() * quaternion.w() + quaternion.x() * quaternion.x() + quaternion.y() * quaternion.y() + quaternion.z() * quaternion.z());
    }
    
    public static float magnitudeSq(Quaternionf quaternion) {
        return quaternion.w() * quaternion.w() + quaternion.x() * quaternion.x() + quaternion.y() * quaternion.y() + quaternion.z() * quaternion.z();
    }
    
    public static Quaternionf getViewRotation(float pitch, float yaw) {
        Quaternionf r1 = new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 0, 0), pitch);
        Quaternionf r2 = new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), yaw + 180);
        return CompatMath.hamiltonProduct(r1,r2);
    }
    
    // NOTE the "from" and "to" cannot be opposite
    public static Quaternionf getRotationBetween(Vec3d from, Vec3d to) {
        from = from.normalize();
        to = to.normalize();
        Vec3d axis = from.crossProduct(to).normalize();
        double cos = from.dotProduct(to);
        double angle = Math.acos(cos);
        //System.out.println();
        //System.out.println(new Quaternionf().fromAxisAngleRad((float)axis.x,(float)axis.y,(float)axis.z, (float) angle));
        //System.out.println(CompatMath.getQuat( new Vector3f((float)axis.x,(float)axis.y,(float)axis.z), (float) angle,false));
        return CompatMath.getQuat( new Vector3f((float)axis.x,(float)axis.y,(float)axis.z), (float) angle,false);
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
