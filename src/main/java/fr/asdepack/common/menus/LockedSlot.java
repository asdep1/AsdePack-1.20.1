package fr.asdepack.common.menus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LockedSlot extends Slot {

    public LockedSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPickup(Player pPlayer) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }
}
