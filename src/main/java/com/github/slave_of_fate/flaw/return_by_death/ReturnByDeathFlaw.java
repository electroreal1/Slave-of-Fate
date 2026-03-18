package com.github.slave_of_fate.flaw.return_by_death;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.flaw.registry.Flaw;
import net.minecraft.server.level.ServerPlayer;

public class ReturnByDeathFlaw extends Flaw {
    public ReturnByDeathFlaw(String name) {
        super(name);
    }

    @Override
    public void tick(ServerPlayer player, PlayerSoul soul) {

    }
}
