package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.util.packet.GravityPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GravityVerifierRegistry<T extends GravityPacket> {
    private final Map<ResourceLocation, VerifierFunction<T>> map = new HashMap<>();

    public void register(ResourceLocation id, VerifierFunction<T> func){
        if(map.containsKey(id))
            GravityChangerMod.LOGGER.error(new Exception("Verifier function already set for identifier "+id));
        map.put(id, func);
    }

    @Nullable
    public VerifierFunction<T> get(ResourceLocation id){
        return map.get(id);
    }

    public interface VerifierFunction<V extends GravityPacket>{
        boolean check(ServerPlayer player, FriendlyByteBuf verifierInfo, V packet);
    }
}
