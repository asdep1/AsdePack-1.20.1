package fr.asdepack.command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class PermissionUtil {

    private static LuckPerms luckPerms = null;

    public static void init() {
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (Exception e) {
            luckPerms = null;
        }
    }

    public static boolean hasPermission(ServerPlayer player, String node) {
        if (FMLEnvironment.production) {
            if (player.hasPermissions(4)) return true;
            if (luckPerms == null) {
                player.sendSystemMessage(Component.literal("§cPermission API non disponible. Veuillez contacter un administrateur."));
                return false;
            }

            return luckPerms
                    .getPlayerAdapter(ServerPlayer.class)
                    .getPermissionData(player)
                    .checkPermission(node)
                    .asBoolean();
        } else {
            return player.hasPermissions(4);
        }
    }
}
