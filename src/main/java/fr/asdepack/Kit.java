package fr.asdepack;

import fr.asdepack.command.PermissionUtil;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Getter
public class Kit {
    private final String name;
    private final List<ItemStack> items;
    private final ItemStack icon;
    private final int cost;
    private final String permission;
    private final int cooldown;

    public Kit(String name, List<ItemStack> items, ItemStack icon) {
        this(name, items, icon, 0, "", 0);
    }

    public Kit(String name, List<ItemStack> items, ItemStack icon, int cost, String permission, int cooldown) {
        this.name = name;
        this.items = items;
        this.icon = icon;
        this.cost = cost;
        this.permission = permission;
        this.cooldown = cooldown;
    }


    /*
     * 0: success
     * 1: no permission
     * 2: cooldown
     * 3: not enough money
     */
    public int canGive(ServerPlayer player) {
        if (permission != null &&
                !PermissionUtil.hasPermission(player, permission)) {
            return 1;
        }

        if (KitCooldownManager.isOnCooldown(player, this)) {
            return 2;
        }

        if (Asdepack.VAULT_ADAPTER.getBalance(player) < cost) {
            return 3;
        }

        return 0;
    }
}
