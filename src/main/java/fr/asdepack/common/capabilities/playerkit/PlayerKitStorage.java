package fr.asdepack.common.capabilities.playerkit;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerKitStorage implements IPlayerKitStorage, INBTSerializable<CompoundTag> {
    Map<Integer, Long> usedKits;

    public PlayerKitStorage() {
        this.usedKits = new HashMap<>();
    }

    @Override
    public Map<Integer, Long> getUsedKits() {
        return usedKits;
    }

    @Override
    public void setUsedKits(Map<Integer, Long> usedKits) {
        this.usedKits = usedKits;
    }

    @Override
    public void markKitUsed(int kitId, long timestamp) {
        this.usedKits.put(kitId, timestamp);
    }

    @Override
    public void copyFrom(IPlayerKitStorage source) {
        this.usedKits = source.getUsedKits();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        for (var entry : this.usedKits.entrySet()) {
            nbt.putLong(String.valueOf(entry.getKey()), entry.getValue());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            int kitId = Integer.parseInt(key);
            long timestamp = nbt.getLong(key);
            this.markKitUsed(kitId, timestamp);

        }
    }
}
