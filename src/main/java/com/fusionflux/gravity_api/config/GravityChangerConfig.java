package com.fusionflux.gravity_api.config;


import eu.midnightdust.lib.config.MidnightConfig;
@SuppressWarnings("CanBeFinal")
public class GravityChangerConfig extends MidnightConfig {

    @Entry @Client  public static boolean keepWorldLook = false;
    @Entry @Client  public static int rotationTime = 500;

    @Entry public static boolean server;
    @Entry public static boolean worldVelocity = false;

    @Entry public static double worldDefaultGravityStrength = 1;
    @Entry public static boolean resetGravityOnDimensionChange = true;
    @Entry public static boolean resetGravityOnRespawn = true;
    @Entry public static boolean voidDamageAboveWorld = false;
}
