package com.github.slave_of_fate.damagesources;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SlaveOfFateDamageSources {
    public static final ResourceKey<DamageType> FLAW_DAMAGE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath("slave_of_fate", "flaw_damage")
    );

    public static void applyFlawDamage(LivingEntity entity, float amount) {
        Level level = entity.level();
        Holder<DamageType> holder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(FLAW_DAMAGE);

        entity.hurt(new DamageSource(holder), amount);
    }
}
