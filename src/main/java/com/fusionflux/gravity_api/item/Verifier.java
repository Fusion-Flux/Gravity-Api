package com.fusionflux.gravity_api.item;

import com.fusionflux.gravity_api.GravityChangerMod;
import com.fusionflux.gravity_api.util.packet.DefaultGravityPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Verifier {
    public static ResourceLocation FIELD_GRAVITY_SOURCE = GravityChangerMod.asResource("changer_item");

    public static boolean check(ServerPlayer player, FriendlyByteBuf info, DefaultGravityPacket packet) {
        if (packet.direction == null)
            return false;
        BlockPos blockPos = info.readBlockPos();
        Level level = player.level();
        if (level == null)
            return false;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        BlockState blockState = level.getBlockState(blockPos);
        /*Return true if the block is a field generator or plating and could have triggered the gravity change.*/
        return true;
    }

    public static FriendlyByteBuf packInfo(BlockPos block){
        var buf = PacketByteBufs.create();
        buf.writeBlockPos(block);
        return buf;
    }

}
