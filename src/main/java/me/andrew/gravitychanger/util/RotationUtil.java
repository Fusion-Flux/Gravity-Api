package me.andrew.gravitychanger.util;

import me.andrew.gravitychanger.GravityChangerMod;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private static final int EXPIRATION_TIME = GravityChangerMod.config.rotationTime;
    private static final List<Rotation> ROTATION_QUEUE = new ArrayList<>();
    private static EndRotation END_ROTATION = new EndRotation(null,null);

    private record Rotation(Quaternion startQuaternion,Quaternion endQuaternion, long expiration) {
    }

    private record EndRotation(Quaternion endpoint,Quaternion startpoint) {
    }

    public static void applyNewRotation(Direction currentDirection,Direction prevDirection) {
        long now = System.currentTimeMillis();
        Quaternion rotStart = WORLD_ROTATION_QUATERNIONS[prevDirection.getId()];
        if (prevDirection == Direction.DOWN) {
            if (currentDirection == Direction.EAST) {
                rotStart = rotationByRadians(Direction.DOWN.getUnitVector(),Math.toRadians(-90));
            }
        }

        if(prevDirection == Direction.UP){
            if (currentDirection == Direction.EAST) {
                rotStart = Vec3f.NEGATIVE_Y.getDegreesQuaternion(-90);
                rotStart.hamiltonProduct(Vec3f.NEGATIVE_Z.getDegreesQuaternion(180));
                //rotStart = rotationByRadians(Direction.DOWN.getUnitVector(),Math.toRadians(-90));
                //rotStart = add(rotStart,rotationByRadians(Direction.NORTH.getUnitVector(),Math.toRadians(90)));
                //rotStart = Vec3f.NEGATIVE_Y.getDegreesQuaternion(GravityChangerMod.config.qX);
                //rotStart.hamiltonProduct(Vec3f.NEGATIVE_X.getDegreesQuaternion(GravityChangerMod.config.qY));
                //rotStart.hamiltonProduct(Vec3f.NEGATIVE_Z.getDegreesQuaternion(GravityChangerMod.config.qZ));
            }
        }

        if (prevDirection == Direction.EAST) {
            if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                rotStart = rotationByRadians(Direction.NORTH.getUnitVector(),Math.toRadians(90));
            }
        }

        if (prevDirection == Direction.UP || prevDirection == Direction.DOWN) {
            if (currentDirection == Direction.WEST) {
                rotStart = rotationByRadians(Direction.DOWN.getUnitVector(),Math.toRadians(90));
                if(prevDirection == Direction.UP)
                rotStart.hamiltonProduct(Vec3f.NEGATIVE_Z.getDegreesQuaternion(180));
            }
        }
//
        if (prevDirection == Direction.WEST) {
            if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                rotStart = rotationByRadians(Direction.NORTH.getUnitVector(),Math.toRadians(-90));
            }
        }
//
        if (prevDirection == Direction.DOWN) {
            if (currentDirection == Direction.SOUTH) {
                rotStart = rotationByRadians(Direction.DOWN.getUnitVector(),Math.toRadians(-180));
            }
        }
//
        if (prevDirection == Direction.UP) {
            if (currentDirection == Direction.NORTH) {
                rotStart = Vec3f.NEGATIVE_Y.getDegreesQuaternion(0);
                rotStart.hamiltonProduct(Vec3f.NEGATIVE_X.getDegreesQuaternion(180));
                //rotStart = rotationByRadians(Direction.UP.getUnitVector(),Math.toRadians(-180));
            }
        }
//
        if (prevDirection == Direction.SOUTH) {
            if (currentDirection == Direction.DOWN) {
                rotStart = rotationByRadians(Direction.EAST.getUnitVector(),Math.toRadians(90));
            }
        }
//
        if (prevDirection == Direction.NORTH) {
            if (currentDirection == Direction.UP) {
                rotStart = Vec3f.NEGATIVE_Y.getDegreesQuaternion(180);
                rotStart.hamiltonProduct(Vec3f.NEGATIVE_X.getDegreesQuaternion(90));
            }
        }

        if (prevDirection == Direction.NORTH) {
            if (currentDirection == Direction.SOUTH) {
                rotStart = Vec3f.NEGATIVE_Y.getDegreesQuaternion(180);
                rotStart.hamiltonProduct(Vec3f.NEGATIVE_X.getDegreesQuaternion(90));
                rotStart.hamiltonProduct(Vec3f.NEGATIVE_Z.getDegreesQuaternion(180));
            }
        }
        if (prevDirection == Direction.SOUTH) {
            if (currentDirection == Direction.NORTH) {
                rotStart = Vec3f.NEGATIVE_Y.getDegreesQuaternion(180);
                rotStart.hamiltonProduct(Vec3f.NEGATIVE_X.getDegreesQuaternion(-90));
            }
        }
        ROTATION_QUEUE.add(new Rotation(WORLD_ROTATION_QUATERNIONS[currentDirection.getId()],rotStart, now + EXPIRATION_TIME));
        END_ROTATION = new EndRotation(WORLD_ROTATION_QUATERNIONS[currentDirection.getId()],rotStart);
    }

    public static Quaternion rotationByRadians(
            Vec3f axis,
            double rotationAngle
    ) {
        double s = Math.sin(rotationAngle / 2.0F);
        return new Quaternion(
                axis.getX() * (float)s,
                axis.getY() * (float)s,
                axis.getZ() * (float)s,
                (float)Math.cos(rotationAngle / 2.0F)
        );
    }

    public static Quaternion getRotation(Direction currentDirec) {
        if(END_ROTATION.endpoint == null){
            END_ROTATION = new EndRotation(WORLD_ROTATION_QUATERNIONS[currentDirec.getId()],WORLD_ROTATION_QUATERNIONS[currentDirec.getId()]);
        }
        if(ROTATION_QUEUE.isEmpty()) return END_ROTATION.endpoint;
        long now = System.currentTimeMillis();

        // Start lerping rotations
        Quaternion accumulator = END_ROTATION.endpoint;

        Iterator<Rotation> iterator = ROTATION_QUEUE.iterator();

        while (iterator.hasNext()) {
            Rotation rotation = iterator.next();

            if (rotation.expiration > now) {
                float delta = (rotation.expiration - now) / (float) EXPIRATION_TIME;
                accumulator = interpolate(rotation.startQuaternion, rotation.endQuaternion, MathHelper.clamp((delta*delta*(3-2*delta)), 0, 1));
            } else {
                iterator.remove();
            }
        }

        return accumulator;
    }

    public static Quaternion multiply(Quaternion quat ,float val) {
        return new Quaternion(
                quat.getX() * val, quat.getY() * val, quat.getZ() * val, quat.getW() * val
        );
    }

    public static Quaternion add(Quaternion a , Quaternion q) {
        return new Quaternion(
                a.getX() + q.getX(), a.getY() + q.getY(), a.getZ() + q.getZ(), a.getW() + q.getW()
        );
    }

    public static float dotProduct(Quaternion a,Quaternion q) {
        return a.getX() * q.getX() +
                a.getY() * q.getY() +
                a.getZ() * q.getZ() +
                a.getW() * q.getW();
    }

    public static Quaternion getNormalized(Quaternion a) {
        float lenSq = dotProduct(a,a);
        if (lenSq != 0) {
            // no fastInverseSqrt. precision is the most important
            double len = Math.sqrt(lenSq);
            return multiply(a,1.0F / (float)len);
        }
        else {
            return new Quaternion(0, 0, 0, 0);
        }
    }

    public static Quaternion interpolate(Quaternion a, Quaternion b, float t) {

        float dot = dotProduct(a,b);

        if (dot < 0.0f) {
            a = multiply(a,-1f);

            dot = -dot;
        }

        float DOT_THRESHOLD = 0.9995F;
        if (dot > DOT_THRESHOLD) {
            // If the inputs are too close for comfort, linearly interpolate
            // and normalize the result.
            //add(multiply(a,1 - t),multiply(b,t));
            return getNormalized(add(multiply(a,1 - t),multiply(b,t)));
        }

        float theta_0 = (float)Math.acos(dot);
        float theta = theta_0 * t;
        float sin_theta = (float)Math.sin(theta);
        float sin_theta_0 = (float)Math.sin(theta_0);

        float s0 = (float)Math.cos(theta) - dot * sin_theta / sin_theta_0;
        float s1 = sin_theta / sin_theta_0;
        return add(multiply(a,s0),multiply(b,s1));
    }

}
