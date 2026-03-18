package com.github.slave_of_fate.flaw.return_by_death;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class ReturnByDeathData extends SavedData {
    public CompoundTag snapshot = new CompoundTag();
    public boolean hasCheckpoint = false;

    public static ReturnByDeathData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ReturnByDeathData::new, ReturnByDeathData::load),
                "return_by_death_data"
        );
    }

    private ReturnByDeathData() {
    }

    public static ReturnByDeathData load(CompoundTag nbt, HolderLookup.Provider provider) {
        ReturnByDeathData data = new ReturnByDeathData();
        data.snapshot = nbt.getCompound("Snapshot");
        data.hasCheckpoint = nbt.getBoolean("HasCheckpoint");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.put("Snapshot", snapshot);
        nbt.putBoolean("HasCheckpoint", hasCheckpoint);
        return nbt;
    }
}
