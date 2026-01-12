package fr.asdepack;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitCooldownManager {

    // kitName -> (playerUUID -> lastUseMillis)
    private static final Map<String, Map<UUID, Long>> COOLDOWNS = new HashMap<>();

    public static boolean isOnCooldown(ServerPlayer player, Kit kit) {
        if (kit.getCooldown() <= 0) return false;

        Map<UUID, Long> map = COOLDOWNS.get(kit.getName());
        if (map == null) return false;

        Long lastUse = map.get(player.getUUID());
        if (lastUse == null) return false;

        long elapsed = (System.currentTimeMillis() - lastUse) / 1000;
        return elapsed < kit.getCooldown();
    }

    public static long getRemaining(ServerPlayer player, Kit kit) {
        Map<UUID, Long> map = COOLDOWNS.get(kit.getName());
        if (map == null) return 0;

        Long lastUse = map.get(player.getUUID());
        if (lastUse == null) return 0;

        long elapsed = (System.currentTimeMillis() - lastUse) / 1000;
        return Math.max(0, kit.getCooldown() - elapsed);
    }

    public static void markUsed(ServerPlayer player, Kit kit) {
        COOLDOWNS
                .computeIfAbsent(kit.getName(), k -> new HashMap<>())
                .put(player.getUUID(), System.currentTimeMillis());
    }
}
