package fr.asdepack.common.menus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TakeableSlot extends Slot {
    public TakeableSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public boolean mayPickup(Player pPlayer) {
        return true;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }
}
