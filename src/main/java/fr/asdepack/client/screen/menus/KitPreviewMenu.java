package fr.asdepack.client.screen.menus;

import fr.asdepack.client.screen.components.slots.ReadOnlySlot;
import fr.asdepack.types.Kit;
import lombok.Getter;
import lombok.Setter;
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

public class KitPreviewMenu extends AbstractContainerMenu {
    public static final MenuType<KitPreviewMenu> TYPE = IForgeMenuType.create((windowId, inv, data) -> {
        List<Kit> clientKits = new ArrayList<>();
        int size = data.readInt();

        for (int i = 0; i < size; i++) {
            Kit kit = Kit.Serializer.fromJson(data.readUtf());
            clientKits.add(kit);
        }

        return new KitPreviewMenu(windowId, inv, clientKits);
    });
    @Getter
    @Setter
    public List<Kit> kits;

    @Getter
    private Kit selectedKit;

    private final SimpleContainer previewContainer;

    public KitPreviewMenu(int id, Inventory playerInv, List<Kit> kits) {
        super(TYPE, id);
        this.kits = kits;

        this.previewContainer = new SimpleContainer(54);

        int index = 0;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new ReadOnlySlot(
                        previewContainer,
                        index++,
                        8 + col * 18,
                        18 + row * 18
                ));
            }
        }

        int i = (6 - 4) * 18;

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInv, (j1 + l * 9) + 9, 8 + j1 * 18, 104 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInv, i1, 8 + i1 * 18, 162 + i));
        }
    }

    public void showKit(Kit kit) {
        this.selectedKit = kit;
        previewContainer.clearContent();
        int i = 0;
        for (ItemStack stack : kit.getItems()) {
            if (i >= previewContainer.getContainerSize()) break;
            previewContainer.setItem(i++, stack.copy());
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
