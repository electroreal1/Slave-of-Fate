package com.github.slave_of_fate.capabilities;

import net.minecraft.util.StringRepresentable;

public enum SoulClass implements StringRepresentable {
    BEAST("Beast", 1),
    MONSTER("Monster", 2),
    DEMON("Demon", 3),
    DEVIL("Devil", 4),
    TYRANT("Tyrant", 5),
    TERROR("Terror", 6),
    TITAN("Titan", 7);

    private final String name;
    private final int coreCount;

    SoulClass(String name, int coreCount) {
        this.name = name;
        this.coreCount = coreCount;
    }

    @Override
    public String getSerializedName() {
        return this.name.toLowerCase();
    }

    public static SoulClass fromCores(int count) {
        for (SoulClass sc : values()) {
            if (sc.coreCount == count) return sc;
        }
        return BEAST;
    }
}
