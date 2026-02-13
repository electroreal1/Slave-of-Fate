package com.github.slave_of_fate.capabilities;

import net.minecraft.util.StringRepresentable;

public enum SoulRank implements StringRepresentable {
    DREAMER("Dreamer", -1, 0),
    DORMANT("Dormant", 0, 100),
    AWAKENED("Awakened", 1, 500),
    ASCENDED("Ascended", 2, 2000),
    TRANSCENDED("Transcended", 3, 10000),
    SUPREME("Supreme", 4, 50000),
    SACRED("Sacred", 5, 250000),
    DIVINE("Divine", 6, 1000000);

    private final String name;
    private final int index;
    private final int baseEssence;

    SoulRank(String name, int index, int baseEssence) {
        this.name = name;
        this.index = index;
        this.baseEssence = baseEssence;
    }

    public String getName() {
        return name;
    }
    public int getIndex() {
        return index;
    }
    public int getBaseEssence() {
        return baseEssence;
    }

    @Override
    public String getSerializedName() {
        return this.name.toLowerCase();
    }
}
