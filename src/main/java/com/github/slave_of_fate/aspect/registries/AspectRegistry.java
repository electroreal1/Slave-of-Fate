package com.github.slave_of_fate.aspect.registries;

import com.github.slave_of_fate.SlaveOfFate;
import com.github.slave_of_fate.aspect.TestAspect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AspectRegistry {
    public static final ResourceKey<Registry<Aspect>> KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(SlaveOfFate.MODID, "aspects"));

    public static final DeferredRegister<Aspect> ASPECTS =
            DeferredRegister.create(KEY, SlaveOfFate.MODID);

    public static final Registry<Aspect> REGISTRY = ASPECTS.makeRegistry(
            builder -> builder.sync(true)
    );

    public static final DeferredHolder<Aspect, TestAspect> TEST_ASPECT =
            AspectRegistry.ASPECTS.register("test_aspect", () -> new TestAspect("test_aspect", AspectRank.DIVINE));
}
