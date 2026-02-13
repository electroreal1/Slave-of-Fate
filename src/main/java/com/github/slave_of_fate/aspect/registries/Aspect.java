package com.github.slave_of_fate.aspect.registries;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class Aspect {
    private final String name;
    private final AspectRank rank;

    public Aspect(String name, AspectRank rank) {
        this.name = name;
        this.rank = rank;
    }

    public abstract void triggerDormant(Player player, PlayerSoul soul);
    public abstract void triggerAwakened(Player player, PlayerSoul soul);
    public abstract void triggerAscended(Player player, PlayerSoul soul);
    public abstract void triggerTranscended(Player player, PlayerSoul soul);
    public abstract void triggerSupreme(Player player, PlayerSoul soul);
    public abstract void triggerSacred(Player player, PlayerSoul soul);
    public abstract void triggerDivine(Player player, PlayerSoul soul);
    public String getName() {
        return name;
    }
    public AspectRank getAspectRank() {
        return rank;
    }

    public void handleAspectAbilityUse(ServerPlayer player) {
        PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);
        Aspect aspect = AspectRegistry.REGISTRY.get(soul.getAspectId());

        if (aspect == null) return;

        switch (soul.getRank()) {
            case DORMANT -> aspect.triggerDormant(player, soul);
            case AWAKENED -> aspect.triggerAwakened(player, soul);
            case ASCENDED -> aspect.triggerAscended(player, soul);
            case TRANSCENDED -> aspect.triggerTranscended(player, soul);
            case SUPREME -> aspect.triggerSupreme(player, soul);
            case SACRED -> aspect.triggerSacred(player, soul);
            case DIVINE -> aspect.triggerDivine(player, soul);
            default -> player.sendSystemMessage(Component.literal("Your soul is too weak to manifest an ability."));
        }
    }

}
