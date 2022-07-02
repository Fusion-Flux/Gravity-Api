package com.fusionflux.gravity_api.api;

import com.fusionflux.gravity_api.GravityChangerMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class GravityVerifier {
    private static final HashMap<Identifier, AddGravityVerifier> addGravityMap = new HashMap<>();
    private static final HashMap<Identifier, SetDefaultGravityVerifier> setDefaultGravityMap = new HashMap<>();
    private static final HashMap<Identifier, SetGravityVerifier> setGravityMap = new HashMap<>();
    private static final HashMap<Identifier, SetInvertedVerifier> setInvertedMap = new HashMap<>();

    public static void registerForAddGravity(Identifier id, AddGravityVerifier func){
        if(addGravityMap.containsKey(id))
            GravityChangerMod.LOGGER.error("AddGravityVerifier function already set for identifier "+id, new Exception());
        addGravityMap.put(id, func);
    }
    public static void registerForSetDefaultGravity(Identifier id, SetDefaultGravityVerifier func){
        if(setDefaultGravityMap.containsKey(id))
            GravityChangerMod.LOGGER.error("SetDefaultGravityVerifier function already set for identifier "+id, new Exception());
        setDefaultGravityMap.put(id, func);
    }
    public static void registerForSetGravity(Identifier id, SetGravityVerifier func){
        if(setGravityMap.containsKey(id))
            GravityChangerMod.LOGGER.error("SetGravityVerifier function already set for identifier "+id, new Exception());
        setGravityMap.put(id, func);
    }
    public static void registerForSetInverted(Identifier id, SetInvertedVerifier func){
        if(setInvertedMap.containsKey(id))
            GravityChangerMod.LOGGER.error("SetInvertedVerifier function already set for identifier "+id, new Exception());
        setInvertedMap.put(id, func);
    }

    @Nullable
    public static AddGravityVerifier getForAddGravity(Identifier id){
        return addGravityMap.get(id);
    }
    @Nullable
    public static SetDefaultGravityVerifier getForSetDefaultGravity(Identifier id){
        return setDefaultGravityMap.get(id);
    }
    @Nullable
    public static SetGravityVerifier getForSetGravity(Identifier id){
        return setGravityMap.get(id);
    }
    @Nullable
    public static SetInvertedVerifier getForSetInverted(Identifier id){
        return setInvertedMap.get(id);
    }

    public interface AddGravityVerifier{ boolean check(ServerPlayerEntity player, PacketByteBuf buf, Gravity gravity);}
    public interface SetDefaultGravityVerifier{ boolean check(ServerPlayerEntity player, PacketByteBuf buf, Direction direction);}
    public interface SetGravityVerifier{ boolean check(ServerPlayerEntity player, PacketByteBuf buf, ArrayList<Gravity> gravityList);}
    public interface SetInvertedVerifier{ boolean check(ServerPlayerEntity player, PacketByteBuf buf, boolean inverted);}
}
