package com.github.slave_of_fate.network;

import com.github.slave_of_fate.SlaveOfFate;
import com.github.slave_of_fate.network.payload.AbilityPayload;
import com.github.slave_of_fate.network.payload.BlackoutPayload;
import com.github.slave_of_fate.network.payload.CheckStatsPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SlaveOfFate.MODID)
public class SlaveOfFateNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(AbilityPayload.TYPE, AbilityPayload.STREAM_CODEC, AbilityPayload::handleData);
        registrar.playToServer(BlackoutPayload.TYPE, BlackoutPayload.STREAM_CODEC, BlackoutPayload::handleData);
        registrar.playToServer(CheckStatsPayload.TYPE, CheckStatsPayload.STREAM_CODEC, CheckStatsPayload::handleData);
    }
}
