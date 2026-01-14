package fr.asdepack.capabilities.playerkit;

import java.util.Map;

public interface IPlayerKitStorage {
    Map<Integer, Long> getUsedKits();
    void setUsedKits(Map<Integer, Long> usedKits);
    void markKitUsed(int kitId, long timestamp);
    void copyFrom(IPlayerKitStorage source);
}
