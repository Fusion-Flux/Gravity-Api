package me.andrew.gravitychanger.util;

import net.minecraft.util.math.*;

public abstract class RotationUtil {
    private static final Direction[][] DIR_WORLD_TO_PLAYER = new Direction[6][];
    static {
        for(Direction gravityDirection : Direction.values()) {
            DIR_WORLD_TO_PLAYER[gravityDirection.getId()] = new Direction[6];
            for(Direction direction : Direction.values()) {
                Vec3d directionVector = Vec3d.of(direction.getVector());
                directionVector = RotationUtil.vecWorldToPlayer(directionVector, gravityDirection);
                DIR_WORLD_TO_PLAYER[gravityDirection.getId()][direction.getId()] = Direction.fromVector(new BlockPos(directionVector));
            }
        }
    }

    public static Direction dirWorldToPlayer(Direction direction, Direction gravityDirection) {
        return DIR_WORLD_TO_PLAYER[gravityDirection.getId()][direction.getId()];
    }

    private static final Direction[][] DIR_PLAYER_TO_WORLD = new Direction[6][];
    static {
        for(Direction gravityDirection : Direction.values()) {
            DIR_PLAYER_TO_WORLD[gravityDirection.getId()] = new Direction[6];
            for(Direction direction : Direction.values()) {
                Vec3d directionVector = Vec3d.of(direction.getVector());
                directionVector = RotationUtil.vecPlayerToWorld(directionVector, gravityDirection);
                DIR_PLAYER_TO_WORLD[gravityDirection.getId()][direction.getId()] = Direction.fromVector(new BlockPos(directionVector));
            }
        }
    }

    public static Direction dirPlayerToWorld(Direction direction, Direction gravityDirection) {
        return DIR_PLAYER_TO_WORLD[gravityDirection.getId()][direction.getId()];
    }

    public static Vec3d vecWorldToPlayer(double x, double y, double z, Direction gravityDirection) {
        return switch(gravityDirection) {
            case DOWN  -> new Vec3d( x,  y,  z);
            case UP    -> new Vec3d(-x, -y,  z);
            case NORTH -> new Vec3d( x,  z, -y);
            case SOUTH -> new Vec3d(-x, -z, -y);
            case WEST  -> new Vec3d(-z,  x, -y);
            case EAST  -> new Vec3d( z, -x, -y);
        };
    }

    public static Vec3d vecWorldToPlayer(Vec3d vec3d, Direction gravityDirection) {
        return vecWorldToPlayer(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }

    public static Vec3d vecPlayerToWorld(double x, double y, double z, Direction gravityDirection) {
        return switch(gravityDirection) {
            case DOWN  -> new Vec3d( x,  y,  z);
            case UP    -> new Vec3d(-x, -y,  z);
            case NORTH -> new Vec3d( x, -z,  y);
            case SOUTH -> new Vec3d(-x, -z, -y);
            case WEST  -> new Vec3d( y, -z, -x);
            case EAST  -> new Vec3d(-y, -z,  x);
        };
    }

    public static Vec3d vecPlayerToWorld(Vec3d vec3d, Direction gravityDirection) {
        return vecPlayerToWorld(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }

    public static Vec3f vecWorldToPlayer(float x, float y, float z, Direction gravityDirection) {
        return switch(gravityDirection) {
            case DOWN  -> new Vec3f( x,  y,  z);
            case UP    -> new Vec3f(-x, -y,  z);
            case NORTH -> new Vec3f( x,  z, -y);
            case SOUTH -> new Vec3f(-x, -z, -y);
            case WEST  -> new Vec3f(-z,  x, -y);
            case EAST  -> new Vec3f( z, -x, -y);
        };
    }

    public static Vec3f vecWorldToPlayer(Vec3f vec3f, Direction gravityDirection) {
        return vecWorldToPlayer(vec3f.getX(), vec3f.getY(), vec3f.getZ(), gravityDirection);
    }

    public static Vec3f vecPlayerToWorld(float x, float y, float z, Direction gravityDirection) {
        return switch(gravityDirection) {
            case DOWN  -> new Vec3f( x,  y,  z);
            case UP    -> new Vec3f(-x, -y,  z);
            case NORTH -> new Vec3f( x, -z,  y);
            case SOUTH -> new Vec3f(-x, -z, -y);
            case WEST  -> new Vec3f( y, -z, -x);
            case EAST  -> new Vec3f(-y, -z,  x);
        };
    }

    public static Vec3f vecPlayerToWorld(Vec3f vec3f, Direction gravityDirection) {
        return vecPlayerToWorld(vec3f.getX(), vec3f.getY(), vec3f.getZ(), gravityDirection);
    }

    public static Vec3d maskWorldToPlayer(double x, double y, double z, Direction gravityDirection) {
        return switch(gravityDirection) {
            case DOWN , UP    -> new Vec3d(x, y, z);
            case NORTH, SOUTH -> new Vec3d(x, z, y);
            case WEST , EAST  -> new Vec3d(z, x, y);
        };
    }

    public static Vec3d maskWorldToPlayer(Vec3d vec3d, Direction gravityDirection) {
        return maskWorldToPlayer(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }

    public static Vec3d maskPlayerToWorld(double x, double y, double z, Direction gravityDirection) {
        return switch(gravityDirection) {
            case DOWN , UP    -> new Vec3d(x, y, z);
            case NORTH, SOUTH -> new Vec3d(x, z, y);
            case WEST , EAST  -> new Vec3d(y, z, x);
        };
    }

    public static Vec3d maskPlayerToWorld(Vec3d vec3d, Direction gravityDirection) {
        return maskPlayerToWorld(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }

    public static Box boxWorldToPlayer(Box box, Direction gravityDirection) {
        return new Box(
                RotationUtil.vecWorldToPlayer(box.minX, box.minY, box.minZ, gravityDirection),
                RotationUtil.vecWorldToPlayer(box.maxX, box.maxY, box.maxZ, gravityDirection)
        );
    }

    public static Box boxPlayerToWorld(Box box, Direction gravityDirection) {
        return new Box(
                RotationUtil.vecPlayerToWorld(box.minX, box.minY, box.minZ, gravityDirection),
                RotationUtil.vecPlayerToWorld(box.maxX, box.maxY, box.maxZ, gravityDirection)
        );
    }

    public static Vec2f rotWorldToPlayer(float yaw, float pitch, Direction gravityDirection) {
        Vec3d vec3d = RotationUtil.vecWorldToPlayer(rotToVec(yaw, pitch), gravityDirection);
        return vecToRot(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec2f rotWorldToPlayer(Vec2f vec2f, Direction gravityDirection) {
        return rotWorldToPlayer(vec2f.x, vec2f.y, gravityDirection);
    }

    public static Vec2f rotPlayerToWorld(float yaw, float pitch, Direction gravityDirection) {
        Vec3d vec3d = RotationUtil.vecPlayerToWorld(rotToVec(yaw, pitch), gravityDirection);
        return vecToRot(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec2f rotPlayerToWorld(Vec2f vec2f, Direction gravityDirection) {
        return rotPlayerToWorld(vec2f.x, vec2f.y, gravityDirection);
    }

    private static Vec3d rotToVec(float yaw, float pitch) {
        double radPitch = pitch * 0.017453292;
        double radNegYaw = -yaw * 0.017453292;
        double cosNegYaw = Math.cos(radNegYaw);
        double sinNegYaw = Math.sin(radNegYaw);
        double cosPitch = Math.cos(radPitch);
        double sinPitch = Math.sin(radPitch);
        return new Vec3d(sinNegYaw * cosPitch, -sinPitch, cosNegYaw * cosPitch);
    }

    private static Vec2f vecToRot(double x, double y, double z) {
        double sinPitch = -y;
        double radPitch = Math.asin(sinPitch);
        double cosPitch = Math.cos(radPitch);
        double sinNegYaw = x / cosPitch;
        double cosNegYaw = MathHelper.clamp(z / cosPitch, -1, 1);
        double radNegYaw = Math.acos(cosNegYaw);
        if(sinNegYaw < 0) radNegYaw = Math.PI * 2 - radNegYaw;

        return new Vec2f(MathHelper.wrapDegrees((float)(-radNegYaw) / 0.017453292F), (float)(radPitch) / 0.017453292F);
    }

    private static final Quaternion[] WORLD_ROTATION_QUATERNIONS = new Quaternion[6];
    static {
        WORLD_ROTATION_QUATERNIONS[0] = Quaternion.IDENTITY.copy();

        WORLD_ROTATION_QUATERNIONS[1] = Vec3f.POSITIVE_Z.getDegreesQuaternion(-180);

        WORLD_ROTATION_QUATERNIONS[2] = Vec3f.POSITIVE_X.getDegreesQuaternion(-90);

        WORLD_ROTATION_QUATERNIONS[3] = Vec3f.POSITIVE_X.getDegreesQuaternion(-90);
        WORLD_ROTATION_QUATERNIONS[3].hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(-180));

        WORLD_ROTATION_QUATERNIONS[4] = Vec3f.POSITIVE_X.getDegreesQuaternion(-90);
        WORLD_ROTATION_QUATERNIONS[4].hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));

        WORLD_ROTATION_QUATERNIONS[5] = Vec3f.POSITIVE_X.getDegreesQuaternion(-90);
        WORLD_ROTATION_QUATERNIONS[5].hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(-270));
    }

    public static Quaternion getWorldRotationQuaternion(Direction gravityDirection) {
        return WORLD_ROTATION_QUATERNIONS[gravityDirection.getId()];
    }

    private static final Quaternion[] ENTITY_ROTATION_QUATERNIONS = new Quaternion[6];
    static {
        ENTITY_ROTATION_QUATERNIONS[0] = Quaternion.IDENTITY;

        ENTITY_ROTATION_QUATERNIONS[1] = Vec3f.POSITIVE_Z.getDegreesQuaternion(-180);

        ENTITY_ROTATION_QUATERNIONS[2] = Vec3f.POSITIVE_X.getDegreesQuaternion(90);

        ENTITY_ROTATION_QUATERNIONS[3] = Vec3f.POSITIVE_X.getDegreesQuaternion(-90);
        ENTITY_ROTATION_QUATERNIONS[3].hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(-180));

        ENTITY_ROTATION_QUATERNIONS[4] = Vec3f.POSITIVE_Y.getDegreesQuaternion(90);
        ENTITY_ROTATION_QUATERNIONS[4].hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(90));

        ENTITY_ROTATION_QUATERNIONS[5] = Vec3f.POSITIVE_X.getDegreesQuaternion(90);
        ENTITY_ROTATION_QUATERNIONS[5].hamiltonProduct(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
    }

    public static Quaternion getCameraRotationQuaternion(Direction gravityDirection) {
        return ENTITY_ROTATION_QUATERNIONS[gravityDirection.getId()];
    }
}
