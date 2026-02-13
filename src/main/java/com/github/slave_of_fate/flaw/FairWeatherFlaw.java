package com.github.slave_of_fate.flaw;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.damagesources.SlaveOfFateDamageSources;
import net.minecraft.server.level.ServerPlayer;

public class FairWeatherFlaw extends Flaw{
    public FairWeatherFlaw(String name) {
        super("Fair Weather");
    }

    @Override
    public void tick(ServerPlayer player, PlayerSoul soul) {
        if (player.level().isRainingAt(player.blockPosition())) {
            SlaveOfFateDamageSources.applyFlawDamage(player, 1.0f);
        }
    }
}
