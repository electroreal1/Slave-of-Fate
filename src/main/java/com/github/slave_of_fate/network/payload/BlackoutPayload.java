package com.github.slave_of_fate.network.payload;

import com.github.slave_of_fate.overlays.BlackoutOverlay;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BlackoutPayload(float intensity) implements CustomPacketPayload {
    public static final Type<BlackoutPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("slave_of_fate", "blackout"));

    public static final StreamCodec<ByteBuf, BlackoutPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, BlackoutPayload::intensity,
            BlackoutPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleData(final BlackoutPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            BlackoutOverlay.setIntensity(payload.intensity());
        });
    }
}
