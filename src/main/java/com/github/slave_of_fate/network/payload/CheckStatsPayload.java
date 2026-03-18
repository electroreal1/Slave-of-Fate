package com.github.slave_of_fate.network.payload;

import com.github.slave_of_fate.nightmarespell.NightmareSpell;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CheckStatsPayload() implements CustomPacketPayload {
    public static final Type<CheckStatsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("slave_of_fate", "check_stats"));

    public static final StreamCodec<ByteBuf, CheckStatsPayload> STREAM_CODEC = StreamCodec.unit(new CheckStatsPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handleData(final CheckStatsPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                NightmareSpell.sendRuneStats(player);
            }
        });
    }
}