package fr.asdepack.common.menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StashMenu extends BorderedMenu {

    public StashMenu(int id, Inventory playerInv, Player player) {
        super(id, playerInv, player, new int[]{}, USEABLE_SLOTS);

//        List<ItemStack> stacks = Asdepack.STASHMANAGER.getStash(player.getUUID());
        List<ItemStack> stacks = new ArrayList<>();
        int slot = 0;
        for (ItemStack items : stacks) {
            container.setItem(USEABLE_SLOTS[slot++], items);
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
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot : USEABLE_SLOTS) {
            stacks.add(getSlot(slot).getItem());
        }
//        Asdepack.STASHMANAGER.saveStash(player.getUUID(), stacks);
    }

    @Override
    protected void onMenuClosed(Player player) {

    }
}
