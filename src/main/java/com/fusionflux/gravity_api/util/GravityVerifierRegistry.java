package com.fusionflux.gravity_api.util;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.util.packet.GravityPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class GravityVerifierRegistry<T extends GravityPacket> {
    private final HashMap<Identifier, VerifierFunction<T>> map = new HashMap<>();

    public void register(Identifier id, VerifierFunction<T> func){
        if(map.containsKey(id))
            GravityChangerMod.LOGGER.error(new Exception("Verifier function already set for identifier "+id));
        map.put(id, func);
    }

    @Nullable
    public VerifierFunction<T> get(Identifier id){
        return map.get(id);
    }

    public interface VerifierFunction<V extends GravityPacket>{
        boolean check(ServerPlayerEntity player, PacketByteBuf verifierInfo, V packet);
    }
}
