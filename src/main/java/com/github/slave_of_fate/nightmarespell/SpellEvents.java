package com.github.slave_of_fate.nightmarespell;

import com.github.slave_of_fate.SlaveOfFate;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SlaveOfFate.MODID)
public class SpellEvents {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            NightmareSpell.tickQueue(player);
        }
    }
}

