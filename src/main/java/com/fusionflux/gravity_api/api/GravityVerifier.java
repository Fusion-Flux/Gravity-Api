package com.fusionflux.gravity_api.api;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.util.Gravity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class GravityVerifier<VF> {
    public static final GravityVerifier<AddGravityVerifier> ADD_GRAVITY = new GravityVerifier<>();
    public static final GravityVerifier<SetDefaultGravityVerifier> SET_DEFAULT_GRAVITY = new GravityVerifier<>();
    public static final GravityVerifier<SetGravityVerifier> SET_GRAVITY = new GravityVerifier<>();
    public static final GravityVerifier<SetInvertedVerifier> SET_INVERTED = new GravityVerifier<>();

    private final HashMap<Identifier, VF> map = new HashMap<>();

    public void register(Identifier id, VF func){
        if(map.containsKey(id))
            GravityChangerMod.LOGGER.error("AddGravityVerifier function already set for identifier "+id, new Exception());
        map.put(id, func);
    }

    @Nullable
    public VF get(Identifier id){
        return map.get(id);
    }

    public interface AddGravityVerifier{ boolean check(ServerPlayerEntity player, PacketByteBuf buf, Gravity gravity);}
    public interface SetDefaultGravityVerifier{ boolean check(ServerPlayerEntity player, PacketByteBuf buf, Direction direction);}
    public interface SetGravityVerifier{ boolean check(ServerPlayerEntity player, PacketByteBuf buf, ArrayList<Gravity> gravityList);}
    public interface SetInvertedVerifier{ boolean check(ServerPlayerEntity player, PacketByteBuf buf, boolean inverted);}
}
