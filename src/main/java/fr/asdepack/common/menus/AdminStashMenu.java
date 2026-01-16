package fr.asdepack.common.menus;

import fr.asdepack.server.Server;
import fr.asdepack.server.modules.stash.StashManager;
import fr.asdepack.types.Stash;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminStashMenu extends BorderedMenu {

    private final StashManager stashManager;
    private final String uuid;

    public AdminStashMenu(int id, Inventory playerInv, Player player, String uuid) {
        super(id, playerInv, player, new int[]{}, USEABLE_SLOTS);
        this.stashManager = Server.getDatabaseManager().getStashManager();
        this.uuid = uuid;

        Stash stash = stashManager.getStashByUUID(uuid);
        if (stash != null) {
            List<ItemStack> stacks = stash.getItems();
            int slot = 0;
            for (ItemStack items : stacks) {
                container.setItem(USEABLE_SLOTS[slot++], items);
            }
        }
    }

    @Override
    protected void onLeftClick(int slot, ItemStack stack) {
    }

    @Override
    protected void onRightClick(int slot, ItemStack stack) {
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        super.clicked(slotId, dragType, clickType, player);
        if (!getSlot(slotId).mayPickup(player)) return;

        Stash stash = stashManager.getStashByUUID(this.uuid);
        if (stash == null) return;
        stash.setItems(collectItemsFromSlots());

        try {
            stashManager.addStash(stash);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ItemStack> collectItemsFromSlots() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot : USEABLE_SLOTS) {
            stacks.add(getSlot(slot).getItem());
        }
        return stacks;
    }

    @Override
    protected void onMenuClosed(Player player) {
    }
}
