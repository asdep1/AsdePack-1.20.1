package fr.asdepack.server;

import fr.asdepack.capabilities.playerkit.IPlayerKitStorage;
import fr.asdepack.capabilities.playerkit.PlayerKitStorage;
import fr.asdepack.capabilities.playerkit.PlayerKitStorageProvider;
import fr.asdepack.network.PacketHelper;
import fr.asdepack.network.packets.CSyncKitStorage;
import fr.asdepack.types.Kit;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class KitCooldownManager {

    public static boolean isOnCooldown(ServerPlayer player, Kit kit) {
        if (kit.getCooldown() <= 0) return false;

        Map<Integer, Long> usedKits = player.getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY)
                .map(IPlayerKitStorage::getUsedKits)
                .orElse(new HashMap<>());

        Long lastUse = usedKits.get(kit.getId());
        if (lastUse == null) return false;

        long elapsed = (System.currentTimeMillis() - lastUse) / 1000;
        return elapsed < kit.getCooldown();
    }

    public static long getRemaining(ServerPlayer player, Kit kit) {
        if (kit.getCooldown() <= 0) return 0;

        Map<Integer, Long> usedKits = player.getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY)
                .map(IPlayerKitStorage::getUsedKits)
                .orElse(new HashMap<>());

        Long lastUse = usedKits.get(kit.getId());
        if (lastUse == null) return 0;

        long elapsed = (System.currentTimeMillis() - lastUse) / 1000;
        return Math.max(0, kit.getCooldown() - elapsed);
    }

    public static void markUsed(ServerPlayer player, Kit kit) {
        if (kit.getCooldown() <= 0) return;

        Map<Integer, Long> usedKits = player.getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY)
                .map(IPlayerKitStorage::getUsedKits)
                .orElse(new HashMap<>());

        usedKits.put(kit.getId(), System.currentTimeMillis());
        player.getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY)
                .ifPresent(storage -> storage.setUsedKits(usedKits));

        PacketHelper.sendToPlayer(
                new CSyncKitStorage(
                        (PlayerKitStorage) player.getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY)
                                .orElseThrow(() -> new IllegalStateException("PlayerKitStorage capability not found"))
                ),
                player
        );
    }

    public static Component getCooldownMessage(ServerPlayer player, Kit kit) {
        long remaining = getRemaining(player, kit);
        long minutes = remaining / 60;
        long seconds = remaining % 60;

        if (minutes > 0) {
            return Component.literal("§cVous devez attendre " + minutes + " minute(s) et " + seconds + " second(s) pour utiliser le kit §6" + kit.getName() + "§c à nouveau.");
        } else {
            return Component.literal("§cVous devez attendre " + seconds + " second(s) pour utiliser le kit §6" + kit.getName() + "§c à nouveau.");
        }
    }
}
