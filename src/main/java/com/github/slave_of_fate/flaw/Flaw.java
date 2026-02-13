package com.github.slave_of_fate.flaw;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import net.minecraft.server.level.ServerPlayer;

public abstract class Flaw {
    private final String name;

    public Flaw(String name) {
        this.name = name;
    }

    public abstract void tick(ServerPlayer player, PlayerSoul soul);
    public void onAction(ServerPlayer player, PlayerSoul soul, String actionType) {
    }

    public String getName() {
        return name;
    }
}
