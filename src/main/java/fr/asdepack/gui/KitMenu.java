package fr.asdepack.gui;

import fr.asdepack.Asdepack;
import fr.asdepack.Kit;
import fr.asdepack.KitCooldownManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

        List<Kit> items = Asdepack.KITMANAGER.getAllKits();

        int pageSize = USEABLE_SLOTS.length;
        int startIndex = page * pageSize;

        for (int i = 0; i < pageSize; i++) {
            int globalIndex = startIndex + i;

            if (globalIndex >= items.size()) {
                break;
            }
            String kitName = items.get(globalIndex).getName();
            int slotId = USEABLE_SLOTS[i];
//            container.setItem(slotId, new ItemStack(Items.NAME_TAG).setHoverName(Component.literal(items.get(globalIndex))));
            ItemStack icon = items.get(globalIndex).getIcon().copy().setHoverName(Component.literal(kitName));
            if (items.get(globalIndex).getCost() > 0) {
                icon.getOrCreateTag().putBoolean("hasCost", true); // Optional flag or just use lore
            }
            container.setItem(slotId, icon);
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

        Kit kit = Asdepack.KITMANAGER.getKit(stack.getHoverName().getString());
        if (kit == null) return;

        if (player instanceof ServerPlayer serverPlayer) {
            switch (kit.canGive(serverPlayer)) {
                case 0:
                    for (ItemStack item : kit.getItems()) {
                        player.getInventory().placeItemBackInInventory(item.copy());
                    }
                    Asdepack.VAULT_ADAPTER.withdraw(serverPlayer, kit.getCost());
                    KitCooldownManager.markUsed(serverPlayer, kit);
                    player.sendSystemMessage(Component.literal("§aKit reçu."));
                    player.closeContainer();
                    break;
                case 1:
                    player.sendSystemMessage(Component.literal("§cVous n'avez pas la permission."));
                    break;
                case 2:
                    long remain = KitCooldownManager.getRemaining(serverPlayer, kit);
                    player.sendSystemMessage(Component.literal("§cCooldown: " + remain + "s"));
                    break;
                case 3:
                    player.sendSystemMessage(Component.literal("§cVous n'avez pas assez d'argent (§e" + kit.getCost() + "§c)."));
                    break;
            }
        }
    }

    @Override
    protected void onRightClick(int slot, ItemStack stack) {

    }


    @Override
    protected void onMenuClosed(Player player) {

    }
}
