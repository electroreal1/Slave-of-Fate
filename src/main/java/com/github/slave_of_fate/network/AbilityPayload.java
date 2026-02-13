package com.github.slave_of_fate.network;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AbilityPayload(int abilityType) implements CustomPacketPayload {
    public static final Type<AbilityPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("slave_of_fate", "ability_packet"));

    public static final StreamCodec<ByteBuf, AbilityPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AbilityPayload::abilityType,
            AbilityPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handleData(final AbilityPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);

            // Logic to trigger the aspect based on data.abilityType (0=Dormant, 1=Awakened)
            // Aspect aspect = AspectRegistry.REGISTRY.get(soul.getAspectId());
            // if (data.abilityType == 1) aspect.triggerAwakened(player, soul);
        });
    }
}