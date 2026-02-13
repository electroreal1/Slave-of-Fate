package com.github.slave_of_fate;

import com.github.slave_of_fate.aspect.registries.AspectRegistry;
import com.github.slave_of_fate.flaw.FlawRegistry;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import com.github.slave_of_fate.registries.SlaveOfFateKeybinds;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SlaveOfFate.MODID)
public class SlaveOfFate {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "slave_of_fate";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "slave_of_fate" namespace
    public SlaveOfFate(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        //BLOCKS.register(modEventBus);
        //ITEMS.register(modEventBus);
        //CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        AspectRegistry.ASPECTS.register(modEventBus);
        FlawRegistry.FLAWS.register(modEventBus);

        SlaveOfFateAttachments.ATTACHMENT_TYPES.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        // modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[Nightmare Spell has loaded successfully.]");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[Nightmare Spell has infected this realm successfully.]");
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SlaveOfFateKeybinds.USE_DORMANT);
        event.register(SlaveOfFateKeybinds.USE_AWAKENED);
    }

}
