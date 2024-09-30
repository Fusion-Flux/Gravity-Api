package com.fusionflux.gravity_api.util;

import net.minecraft.core.Vec3i;
import org.joml.Vector3d;

public class PortingUtils {
    public static Vector3d from(Vec3i vec3i) {
        return new Vector3d(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }
}
