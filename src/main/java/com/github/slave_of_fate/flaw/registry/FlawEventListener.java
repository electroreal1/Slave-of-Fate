package com.github.slave_of_fate.flaw.registry;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.capabilities.SoulRank;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "slave_of_fate")
public class FlawEventListener {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);

            if (soul.getRank().getIndex() >= SoulRank.DORMANT.getIndex()) {
                // Look up the Flaw from a Map/Registry using soul.getAspectId()
                // and call flaw.tick(player, soul);
            }
        }
    }
}
