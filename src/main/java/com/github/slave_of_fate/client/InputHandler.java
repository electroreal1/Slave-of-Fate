package com.github.slave_of_fate.client;

import com.github.slave_of_fate.network.AbilityPayload;
import com.github.slave_of_fate.registries.SlaveOfFateKeybinds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "slave_of_fate", value = Dist.CLIENT)
public class InputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (SlaveOfFateKeybinds.USE_DORMANT.consumeClick()) {
            // Send packet to server for Dormant ability
            PacketDistributor.sendToServer(new AbilityPayload(0));
        }
        if (SlaveOfFateKeybinds.USE_AWAKENED.consumeClick()) {
            PacketDistributor.sendToServer(new AbilityPayload(1));
        }
        if (SlaveOfFateKeybinds.USE_ASCENDED.consumeClick()) {
            PacketDistributor.sendToServer(new AbilityPayload(2));
        }
    }
}
