package fr.asdepack.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class BorderedMenu extends AbstractContainerMenu {

    public static final int SIZE = 9 * 6;
    protected static final int[] USEABLE_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    protected final SimpleContainer container;
    protected final Player player;
    protected final ItemStack BORDER_ITEMSTACK = new ItemStack(Items.GRAY_STAINED_GLASS_PANE).setHoverName(Component.literal(""));
    protected int[] TAKEABLES_SLOTS;
    protected int[] PLACEABLES_SLOTS;

    //TODO : scale useable slot and bottom border with the container size
    public BorderedMenu(int id, Inventory playerInv, Player player, int[] takeableSlot, int[] placeableSlot) {
        super(MenuType.GENERIC_9x6, id);
        this.player = player;
        this.container = new SimpleContainer(SIZE);
        this.TAKEABLES_SLOTS = takeableSlot;
        this.PLACEABLES_SLOTS = placeableSlot;
        for (int i = 0; i < 9; i++) {
            container.setItem(i, BORDER_ITEMSTACK);
        }

        if (SIZE > 9) {
            container.setItem(9, BORDER_ITEMSTACK);
            container.setItem(17, BORDER_ITEMSTACK);

        }
        if (SIZE > 18) {
            container.setItem(18, BORDER_ITEMSTACK);
            container.setItem(26, BORDER_ITEMSTACK);
        }
        if (SIZE > 27) {
            container.setItem(27, BORDER_ITEMSTACK);
            container.setItem(35, BORDER_ITEMSTACK);
        }
        if (SIZE > 36) {
            container.setItem(36, BORDER_ITEMSTACK);
            container.setItem(44, BORDER_ITEMSTACK);
        }
        if (SIZE > 45) {
            for (int i = 45; i < 54; i++) {
                container.setItem(i, BORDER_ITEMSTACK);
            }
        }

        for (int i = 0; i < SIZE; i++) {
            addSlot(new LockedSlot(container, i, 0, 0));
        }
        for (int slot : TAKEABLES_SLOTS) {
            this.slots.set(slot, new TakeableSlot(container, slot, 0, 0));
        }
        for (int slot : PLACEABLES_SLOTS) {
            this.slots.set(slot, new Slot(container, slot, 0, 0));
        }

        // PlayerInv
        int startY = 84;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9,
                        8 + col * 18,
                        startY + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        if (slot instanceof LockedSlot) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < SIZE) {
            // MENU --> INVENTAIRE
            if (!(slot.mayPickup(player))) {
                return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(
                    stack,
                    SIZE,
                    this.slots.size(),
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            // INVENTAIRE --> MENU
            if (!this.moveItemStackTo(
                    stack,
                    0,
                    SIZE,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack,
                                      int startIndex,
                                      int endIndex,
                                      boolean reverse) {

        boolean moved = false;
        int index = reverse ? endIndex - 1 : startIndex;

        while (!stack.isEmpty() &&
                (reverse ? index >= startIndex : index < endIndex)) {

            Slot target = this.slots.get(index);

            if (!(target instanceof LockedSlot)
                    && target.mayPlace(stack)
                    && target.hasItem()) {

                ItemStack targetStack = target.getItem();

                if (ItemStack.isSameItemSameTags(stack, targetStack)) {
                    int max = Math.min(
                            targetStack.getMaxStackSize(),
                            target.getMaxStackSize()
                    );

                    int transferable = Math.min(
                            stack.getCount(),
                            max - targetStack.getCount()
                    );

                    if (transferable > 0) {
                        targetStack.grow(transferable);
                        stack.shrink(transferable);
                        target.setChanged();
                        moved = true;
                    }
                }
            }

            index += reverse ? -1 : 1;
        }

        index = reverse ? endIndex - 1 : startIndex;

        while (!stack.isEmpty() &&
                (reverse ? index >= startIndex : index < endIndex)) {

            Slot target = this.slots.get(index);

            if (!(target instanceof LockedSlot)
                    && target.mayPlace(stack)
                    && !target.hasItem()) {

                target.set(stack.copy());
                stack.setCount(0);
                target.setChanged();
                moved = true;
                break;
            }

            index += reverse ? -1 : 1;
        }

        return moved;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot instanceof TakeableSlot;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < SIZE) {
            ItemStack clicked = container.getItem(slotId);

            if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && dragType == 0) {
                onLeftClick(slotId, clicked);
            }

            if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && dragType == 1) {
                onRightClick(slotId, clicked);

            }
            if (clickType == ClickType.SWAP) {
                if (!getSlot(slotId).mayPickup(player)) return;
            }
        }

        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);

        onMenuClosed(player);
    }

    protected abstract void onMenuClosed(Player player);

    protected abstract void onLeftClick(int slot, ItemStack stack);

    protected abstract void onRightClick(int slot, ItemStack stack);
}
