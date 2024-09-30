package com.fusionflux.gravity_api.util.packet;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.util.GravityComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OverwriteGravityPacket extends GravityPacket {
    public static StreamCodec<ByteBuf, OverwriteGravityPacket> STREAM_CODEC = StreamCodec.composite(
            Gravity.STREAM_CODEC.apply(ByteBufCodecs.list()), p -> p.gravityList,
            ByteBufCodecs.BOOL, p -> p.initialGravity,
            OverwriteGravityPacket::new
    );
    
    public final List<Gravity> gravityList;
    public final boolean initialGravity;

    public OverwriteGravityPacket(List<Gravity> _gravityList, boolean _initialGravity){
        gravityList = _gravityList;
        initialGravity = _initialGravity;
    }

    @Override
    public void run(GravityComponent gc) {
        gc.setGravity(gravityList, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        Optional<Gravity> max = gravityList.stream().max(Comparator.comparingInt(Gravity::priority));
        if(max.isEmpty()) return new RotationParameters();
        return max.get().rotationParameters();
    }
}
