package fr.asdepack.types;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.asdepack.Asdepack;
import fr.asdepack.KitCooldownManager;
import fr.asdepack.command.PermissionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@DatabaseTable(tableName = "kits")
@NoArgsConstructor
@AllArgsConstructor
public class Kit {
    @DatabaseField(id = true)
    @Getter
    @Setter
    private int id;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private String name;
    @DatabaseField(canBeNull = false, persisterClass =  ItemStackPersister.class)
    @Getter
    @Setter
    private ItemStack icon;
    @DatabaseField(canBeNull = false, persisterClass =  ItemStackListPersister.class)
    @Getter
    @Setter
    private List<ItemStack> items;
    @Getter
    @Setter
    @DatabaseField(canBeNull = false)
    private int cost;
    @Getter
    @Setter
    @DatabaseField(canBeNull = false)
    private String permission;
    @Getter
    @Setter
    @DatabaseField(canBeNull = false)
    private int cooldown;

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
