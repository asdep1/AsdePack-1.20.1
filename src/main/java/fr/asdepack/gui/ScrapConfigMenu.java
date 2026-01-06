package fr.asdepack.gui;

import fr.asdepack.Asdepack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ScrapConfigMenu extends BorderedMenu {

    private static final int[] TAKEABLESLOT = {};
    private static final int[] PLACEABLESLOT = {};
    private final List<ItemStack> SCRAPPEDRESULT;
    private final ItemStack SELECTEDITEM;

    public ScrapConfigMenu(int id, Inventory playerInv, Player player, ItemStack item) {
        super(id, playerInv, player, TAKEABLESLOT, USEABLE_SLOTS);
        this.SELECTEDITEM = item;
        this.SCRAPPEDRESULT = Asdepack.SCRAP_MANAGER.getScrapFor(item);
        int slot = 0;
        for (ItemStack items : SCRAPPEDRESULT) {
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
        Asdepack.SCRAP_MANAGER.saveScrap(this.SELECTEDITEM, stacks);
    }

    @Override
    protected void onMenuClosed(Player player) {

    }
}
