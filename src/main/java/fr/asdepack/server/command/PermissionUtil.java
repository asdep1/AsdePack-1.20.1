package fr.asdepack.server.command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

public class PermissionUtil {

    private static LuckPerms luckPerms = null;

    public static void init() {
        try {
            if (ModList.get().isLoaded("luckperms")) {
                System.out.println("[Asdepack] Permission API disponible.");
                luckPerms = LuckPermsProvider.get();
            } else {
                System.out.println("[Asdepack] Permission API indisponible.");
            }
        } catch (IllegalStateException e) {
            luckPerms = null;
            System.out.println("[Asdepack] Permission API indisponible.");
        }
    }

    public static boolean hasPermission(ServerPlayer player, String node) {
        if (player.hasPermissions(4)) return true;
        if (luckPerms == null) {
            return false;
        }

        return luckPerms
                .getPlayerAdapter(ServerPlayer.class)
                .getPermissionData(player)
                .checkPermission(node)
                .asBoolean();
    }
}
