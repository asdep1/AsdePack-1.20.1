package fr.asdepack.client.gui;

import fr.asdepack.Asdepack;
import fr.asdepack.server.KitCooldownManager;
import fr.asdepack.server.Server;
import fr.asdepack.types.Kit;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.sql.SQLException;
import java.util.List;

public class KitMenu extends BorderedMenu {

    private static final int[] TAKEABLESLOT = {};
    private static final int[] PLACEABLESLOT = {};
    private final int page;

    public KitMenu(int id, Inventory playerInv, Player player) throws SQLException {
        this(id, playerInv, player, 0);
    }

    public KitMenu(int id, Inventory playerInv, Player player, int page) throws SQLException {
        super(id, playerInv, player, TAKEABLESLOT, PLACEABLESLOT);

        this.page = page;

        List<Kit> items = Server.getDatabaseManager().getKitManager().getKits();

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
                (id, inv, p) -> {
                    try {
                        return new KitMenu(id, inv, p, this.page + 1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                Component.literal("Kit list")
        ));
    }

    private void lastPage() {
        this.player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> {
                    try {
                        return new KitMenu(id, inv, p, this.page - 1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
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

        Kit kit =  Server.getDatabaseManager().getKitManager().getKitByName(stack.getHoverName().getString());
//        Kit kit = Asdepack.KITMANAGER.getKit(stack.getHoverName().getString());
        if (kit == null) return;

        if (player instanceof ServerPlayer serverPlayer) {
            switch (Kit.canGive(kit, serverPlayer)) {
                case SUCCESS:
                    for (ItemStack item : kit.getItems()) {
                        player.getInventory().placeItemBackInInventory(item.copy());
                    }
                    Asdepack.VAULT_ADAPTER.withdraw(serverPlayer, kit.getCost());
                    KitCooldownManager.markUsed(serverPlayer, kit);
                    player.sendSystemMessage(Component.literal("§aKit reçu."));
                    player.closeContainer();
                    break;
                case NO_PERMISSION:
                    player.sendSystemMessage(Component.literal("§cVous n'avez pas la permission."));
                    break;
                case ON_COOLDOWN:
                    long remain = KitCooldownManager.getRemaining(serverPlayer, kit);
                    player.sendSystemMessage(Component.literal("§cCooldown: " + remain + "s"));
                    break;
                case INSUFFICIENT_FUNDS:
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
