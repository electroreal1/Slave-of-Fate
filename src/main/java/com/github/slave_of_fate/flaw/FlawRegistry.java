package com.github.slave_of_fate.flaw;

import com.github.slave_of_fate.SlaveOfFate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FlawRegistry {
    public static final ResourceKey<Registry<Flaw>> KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(SlaveOfFate.MODID, "flaws"));

    public static final DeferredRegister<Flaw> FLAWS =
            DeferredRegister.create(KEY, SlaveOfFate.MODID);

    public static final Registry<Flaw> REGISTRY = FLAWS.makeRegistry(
            builder -> builder.sync(true)
    );

    public static final DeferredHolder<Flaw, FairWeatherFlaw> FAIR_WEATHER =
            FlawRegistry.FLAWS.register("fair_weather", () -> new FairWeatherFlaw("Fair Weather"));
}
