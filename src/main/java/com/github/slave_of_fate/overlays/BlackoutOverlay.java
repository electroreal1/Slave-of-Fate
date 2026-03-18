package com.github.slave_of_fate.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = "slave_of_fate", value = Dist.CLIENT)
public class BlackoutOverlay {

    public static float intensity = 0.0f;
    private static float targetIntensity = 0.0f;

    public static void setIntensity(float newIntensity) {
        targetIntensity = newIntensity;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        if (intensity <= 0.01f || !event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        if (intensity < targetIntensity) intensity += 0.05f;
        else if (intensity > targetIntensity) intensity -= 0.02f;

        GuiGraphics graphics = event.getGuiGraphics();
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int alpha = (int) (intensity * 255.0f);
        int color = (alpha << 24) | (0x000000);

        graphics.fill(0, 0, width, height, color);
    }
}
