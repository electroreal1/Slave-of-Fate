package com.github.slave_of_fate.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public class PlayerSoul {
    private int essence;
    private int maxEssence;
    private SoulRank rank;
    private int coreCount;
    private ResourceLocation aspectId;
    private ResourceLocation flawId;
    private String trueName;

    public static final Codec<PlayerSoul> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("essence").forGetter(PlayerSoul::getEssence),
                    Codec.INT.fieldOf("maxEssence").forGetter(PlayerSoul::getMaxEssence),
                    StringRepresentable.fromEnum(SoulRank::values).fieldOf("rank").forGetter(PlayerSoul::getRank),
                    Codec.INT.fieldOf("coreCount").forGetter(PlayerSoul::getCoreCount),
                    ResourceLocation.CODEC.fieldOf("aspectId").forGetter(PlayerSoul::getAspectId),
                    ResourceLocation.CODEC.fieldOf("flawId").forGetter(PlayerSoul::getFlawId),
                    Codec.STRING.fieldOf("trueName").forGetter(PlayerSoul::getTrueName)
            ).apply(instance, PlayerSoul::new)
    );

    public PlayerSoul(int essence, int maxEssence, SoulRank rank, int coreCount, ResourceLocation aspectId, ResourceLocation flawId, String trueName) {
        this.essence = essence;
        this.maxEssence = maxEssence;
        this.rank = rank;
        this.coreCount = coreCount;
        this.aspectId = aspectId;
        this.flawId = flawId;
        this.trueName = trueName;
    }

    public PlayerSoul() {
        this(0, 0, SoulRank.DREAMER, 1,
                ResourceLocation.fromNamespaceAndPath("slave_of_fate", "none"),
                ResourceLocation.fromNamespaceAndPath("slave_of_fate", "none"),
                "None");
    }

    public void addEssence(int amount) {
        this.essence = Math.min(this.essence + amount, this.maxEssence);
    }

    public boolean consumeEssence(int amount) {
        if (this.essence >= amount) {
            this.essence -= amount;
            return true;
        }
        return false;
    }

    public int getEssence() {
        return essence;
    }

    public int getCoreCount() {
        return coreCount;
    }

    public int getMaxEssence() {
        return maxEssence;
    }

    public SoulRank getRank() {
        return rank;
    }

    public ResourceLocation getAspectId() {
        return aspectId;
    }

    public ResourceLocation getFlawId() {
        return flawId;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setAspectId(ResourceLocation id) {
        this.aspectId = id;
    }

    public void setFlawId(ResourceLocation id) {
        this.flawId = id;
    }

    public void setTrueName(String name) {
        this.trueName = name;
    }

    public void setRank(SoulRank rank) {
        this.rank = rank;
    }
}
