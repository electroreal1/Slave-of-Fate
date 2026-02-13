package com.github.slave_of_fate.aspect.registries;

public enum AspectRank {
    AWAKENED(1f),
    ASCENDED(1.5f),
    TRANSCENDENT(2.2f),
    SUPREME(4f),
    SACRED(8f),
    DIVINE(15f);

    public final float powerModifier;
    AspectRank(float modifier) {
        this.powerModifier = modifier;
    }
}
