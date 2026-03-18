package com.github.slave_of_fate;

import com.github.slave_of_fate.aspect.registries.AspectRegistry;
import com.github.slave_of_fate.flaw.registry.FlawRegistry;
import com.github.slave_of_fate.flaw.return_by_death.ReturnByDeathEngine;
import com.github.slave_of_fate.nightmarespell.NightmareSpellCommands;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import com.github.slave_of_fate.registries.SlaveOfFateKeybinds;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(SlaveOfFate.MODID)
public class SlaveOfFate {
    public static final String MODID = "slave_of_fate";

    public SlaveOfFate(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);

        AspectRegistry.ASPECTS.register(modEventBus);
        FlawRegistry.FLAWS.register(modEventBus);
        SlaveOfFateAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerKeys);
    }

    private void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SlaveOfFateKeybinds.SHOW_RUNES);
        event.register(SlaveOfFateKeybinds.USE_DORMANT);
        event.register(SlaveOfFateKeybinds.USE_AWAKENED);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        NightmareSpellCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel overworld = event.getServer().overworld();
        ReturnByDeathEngine.loadHistoryFromDisk(overworld);
    }
}