package com.github.slave_of_fate.aspect;

import com.github.slave_of_fate.aspect.registries.Aspect;
import com.github.slave_of_fate.aspect.registries.AspectRank;
import com.github.slave_of_fate.capabilities.PlayerSoul;
import net.minecraft.world.entity.player.Player;

public class TestAspect extends Aspect {
    public TestAspect(String name, AspectRank rank) {
        super("Test Aspect", AspectRank.DIVINE);
    }

    @Override
    public void triggerDormant(Player player, PlayerSoul soul) {

    }

    @Override
    public void triggerAwakened(Player player, PlayerSoul soul) {

    }

    @Override
    public void triggerAscended(Player player, PlayerSoul soul) {

    }

    @Override
    public void triggerTranscended(Player player, PlayerSoul soul) {

    }

    @Override
    public void triggerSupreme(Player player, PlayerSoul soul) {

    }

    @Override
    public void triggerSacred(Player player, PlayerSoul soul) {

    }

    @Override
    public void triggerDivine(Player player, PlayerSoul soul) {

    }


}
