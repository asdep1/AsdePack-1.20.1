package fr.asdepack.gui;

import fr.asdepack.Asdepack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class KitMenu extends BorderedMenu {

    private static final int[] TAKEABLESLOT = {};
    private static final int[] PLACEABLESLOT = {};
    private final int page;

    public KitMenu(int id, Inventory playerInv, Player player) {
        this(id, playerInv, player, 0);
    }

    public KitMenu(int id, Inventory playerInv, Player player, int page) {
        super(id, playerInv, player, TAKEABLESLOT, PLACEABLESLOT);

        this.page = page;

        List<String> items = Asdepack.KITMANAGER.getKitList();

        int pageSize = USEABLE_SLOTS.length;
        int startIndex = page * pageSize;

        for (int i = 0; i < pageSize; i++) {
            int globalIndex = startIndex + i;

            if (globalIndex >= items.size()) {
                break;
            }
            String kitName = items.get(globalIndex);
            int slotId = USEABLE_SLOTS[i];
//            container.setItem(slotId, new ItemStack(Items.NAME_TAG).setHoverName(Component.literal(items.get(globalIndex))));
            container.setItem(slotId, Asdepack.KITMANAGER.getKitIcon(items.get(globalIndex)).setHoverName(Component.literal(kitName)));
        }

        if (items.size() > (page + 1) * pageSize) {
            container.setItem(50,
                    new ItemStack(Items.ARROW)
                            .setHoverName(Component.literal("Next page")));
        }

        if (page > 0) {
            container.setItem(48,
                    new ItemStack(Items.ARROW)
                            .setHoverName(Component.literal("Last page")));
        }
    }

    private void nextPage() {
        this.player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new KitMenu(id, inv, p, this.page + 1),
                Component.literal("Kit list")
        ));
    }

    private void lastPage() {
        this.player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new KitMenu(id, inv, p, this.page - 1),
                Component.literal("Kit list")
        ));
    }

    @Override
    protected void onLeftClick(int slot, ItemStack stack) {
        if (stack == ItemStack.EMPTY || stack == BORDER_ITEMSTACK) {
            return;
        }
        if (slot == 50) {
            nextPage();
            return;
        }
        if (slot == 48) {
            lastPage();
            return;
        }

        List<ItemStack> items = Asdepack.KITMANAGER.getKitFor(stack.getHoverName().getString());
        if (items == null) return;
        for (ItemStack item : items) {
            player.getInventory().placeItemBackInInventory(item);
        }
    }

    @Override
    protected void onRightClick(int slot, ItemStack stack) {

    }


    @Override
    protected void onMenuClosed(Player player) {

    }
}
