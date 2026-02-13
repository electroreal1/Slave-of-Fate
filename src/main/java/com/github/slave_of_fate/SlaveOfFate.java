package com.github.slave_of_fate;

import com.github.slave_of_fate.aspect.registries.AspectRegistry;
import com.github.slave_of_fate.flaw.registry.FlawRegistry;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import com.github.slave_of_fate.registries.SlaveOfFateKeybinds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(SlaveOfFate.MODID)
public class SlaveOfFate {
    public static final String MODID = "slave_of_fate";

    public SlaveOfFate(IEventBus modEventBus, ModContainer modContainer) {

//        NeoForge.EVENT_BUS.register(this);

        AspectRegistry.ASPECTS.register(modEventBus);
        FlawRegistry.FLAWS.register(modEventBus);
        SlaveOfFateAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerKeys);
    }


    public void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SlaveOfFateKeybinds.USE_DORMANT);
        event.register(SlaveOfFateKeybinds.USE_AWAKENED);
    }
}