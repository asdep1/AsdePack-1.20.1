package fr.asdepack.common.menus;

import fr.asdepack.client.screen.components.slots.ReadOnlySlot;
import fr.asdepack.types.Kit;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RadioConfigMenu extends AbstractContainerMenu {
    @Getter
    public final ItemStack radio;
    public final int radio_location;
    public final Inventory inventory;

    public static final MenuType<RadioConfigMenu> TYPE = IForgeMenuType.create((windowId, inv, data) -> new RadioConfigMenu(windowId, inv, data.readInt()));

    public RadioConfigMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, extraData.readInt());
    }

    public RadioConfigMenu(int id, Inventory playerInv, int slotRadioLocation) {
        super(TYPE, id);
        this.radio_location = slotRadioLocation;
        this.inventory = playerInv;
        this.radio = playerInv.getItem(slotRadioLocation);

        int i = 0;

        for(int l = 0; l < 3; ++l) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInv, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInv, i1, 8 + i1 * 18, 109));
        }
    }


    @Override
    public @NotNull ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
