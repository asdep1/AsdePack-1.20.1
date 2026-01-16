package fr.asdepack.common.menus;

import fr.asdepack.server.Server;
import fr.asdepack.types.Scrap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrapMenu extends BorderedMenu {

    private static final int[] TAKEABLESLOT = {};
    private static final int[] PLACEABLESLOT = {};
    private static final ItemStack PENDING_DELETE = new ItemStack(Items.ORANGE_WOOL);

    static {
        PENDING_DELETE.setHoverName(Component.literal("§6Clique pour supprimer"));
        PENDING_DELETE.getOrCreateTag().putBoolean("pendingDelete", true);
    }

    private final int page;
    private final Map<Integer, ItemStack> pendingDelete = new HashMap<>();

    public ScrapMenu(int id, Inventory playerInv, Player player) {
        this(id, playerInv, player, 0);
    }

    public ScrapMenu(int id, Inventory playerInv, Player player, int page) {
        super(id, playerInv, player, TAKEABLESLOT, PLACEABLESLOT);

        this.page = page;

        List<Scrap> items;
        try {
            items = Server.getDatabaseManager().getScrapManager().getScraps();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int pageSize = USEABLE_SLOTS.length;
        int startIndex = page * pageSize;

        for (int i = 0; i < pageSize; i++) {
            int globalIndex = startIndex + i;

            if (globalIndex >= items.size()) {
                break;
            }

            int slotId = USEABLE_SLOTS[i];
            container.setItem(slotId, items.get(globalIndex).getItem().copy());
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

    private void startDeleteTimer(int slot) {
        pendingDelete.put(slot, container.getItem(slot).copy());
        container.setItem(slot, PENDING_DELETE);

        MinecraftServer server = this.player.getServer();
        if (server == null) return;

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }

            server.execute(() -> {
                if (pendingDelete.containsKey(slot)) {
                    container.setItem(slot, pendingDelete.remove(slot));
                }
            });
        }, "Scrap-Confirm-Thread").start();

    }

    private void confirmDelete(int slot) {
        ItemStack original = pendingDelete.remove(slot);
        container.setItem(slot, ItemStack.EMPTY);
        this.setCarried(ItemStack.EMPTY);
        Server.getDatabaseManager().getScrapManager().removeScrap(original);
        this.player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ScrapMenu(id, inv, p, this.page),
                Component.literal("Scrap list")
        ));
    }

    private void nextPage() {
        this.player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ScrapMenu(id, inv, p, this.page + 1),
                Component.literal("Scrap list")
        ));
    }

    private void lastPage() {
        this.player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ScrapMenu(id, inv, p, this.page - 1),
                Component.literal("Scrap list")
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
        this.player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> {
                    try {
                        return new ScrapConfigMenu(id, inv, p, stack);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                Component.literal("Scrap config")
        ));
    }

    @Override
    protected void onRightClick(int slot, ItemStack stack) {
        if (stack == ItemStack.EMPTY || stack == BORDER_ITEMSTACK) {
            return;
        }
        for (int i : USEABLE_SLOTS) {
            if (i == slot) startDeleteTimer(slot);
        }

    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {

        if (slotId >= 0 && slotId < SIZE) {

            ItemStack stack = container.getItem(slotId);

            if (clickType == ClickType.PICKUP && dragType == 1) {

                if (stack.is(Items.ORANGE_WOOL) && pendingDelete.containsKey(slotId)) {
                    confirmDelete(slotId);
                    return;
                }

                if (!stack.isEmpty() && !stack.is(BORDER_ITEMSTACK.getItem())) {
                    startDeleteTimer(slotId);
                    return;
                }
            }

            if (clickType == ClickType.PICKUP && dragType == 0) {
                onLeftClick(slotId, stack);
                return;
            }
        }

        super.clicked(slotId, dragType, clickType, player);
    }


    @Override
    protected void onMenuClosed(Player player) {
        pendingDelete.clear();
    }
}
