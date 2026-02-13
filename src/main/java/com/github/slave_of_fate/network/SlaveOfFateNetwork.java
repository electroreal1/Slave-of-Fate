package com.github.slave_of_fate.network;

import com.github.slave_of_fate.SlaveOfFate;
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
    }
}
