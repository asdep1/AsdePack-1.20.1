package fr.asdepack.common.menus;

import com.corrinedev.gundurability.config.Config;
import fr.asdepack.helpers.ItemKeyUtil;
import fr.asdepack.server.Server;
import fr.asdepack.types.Scrap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrappingMenu extends BorderedMenu {

    private static final ItemStack SCRAP_BUTTON = new ItemStack(Items.GREEN_WOOL).setHoverName(Component.literal("§2Recycler"));
    private List<ItemStack> scrappedResults = new ArrayList<>();
    private List<ItemStack> selectedItems = new ArrayList<>();

    public ScrappingMenu(int id, Inventory playerInv, Player player) {
        super(id, playerInv, player, new int[]{}, USEABLE_SLOTS);

        container.setItem(49, SCRAP_BUTTON);
    }

    public float getDamageRatio(ItemStack stack) {

        if (!stack.isDamageableItem() && !stack.getOrCreateTag().contains("Durability")) {
            return 1.0f;
        }

        double damage;
        double max;

        if (stack.getOrCreateTag().contains("Durability")) {
            var tag = stack.getOrCreateTag();
            double durability = tag.getInt("Durability");
            max = Config.getDurability(tag.getString("GunId"));

            if (max <= 0) return 1.0f;

            damage = max - durability;
        } else {
            max = stack.getMaxDamage();
            damage = stack.getDamageValue();
        }

        if (max <= 0) return 1.0f;

        float ratio = 1.0f - (float) (damage / max);

        return Math.max(0.05f, Math.min(1.0f, ratio));
    }


    @Override
    protected void onLeftClick(int slot, ItemStack stack) throws Exception {
        if (slot == 49) {
            computeScrapFromItems();
            for (ItemStack resultStack : scrappedResults) {
                player.getInventory().placeItemBackInInventory(resultStack);
            }
            for (ItemStack selectedStack : selectedItems) {
                player.getInventory().placeItemBackInInventory(selectedStack);
            }
            scrappedResults = new ArrayList<>();
            selectedItems = new ArrayList<>();
            for (int slotIndex : USEABLE_SLOTS) {
                setItem(slotIndex, 0, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void onRightClick(int slot, ItemStack stack) {
    }

    public void computeScrapFromItems() throws Exception {
        Map<String, ItemStack> resultsMap = new HashMap<>();
        List<ItemStack> itemsToProcess = new ArrayList<>(selectedItems);
        for (ItemStack input : itemsToProcess) {
            if (input.isEmpty()) continue;

            ItemStack compatInput = Scrap.compatTacz(input.copy());
            Scrap scrapData = Server.getDatabaseManager().getScrapManager().getScrapByItem(compatInput);

            if (scrapData.getScraps() == null) {
                continue;
            }

            float ratio = getDamageRatio(input);
            if (ratio <= 0f) continue;

            int inputCount = input.getCount();

            List<ItemStack> scraps = scrapData.getScraps();
            for (ItemStack scrap : scraps) {
                int amount = Math.max(1, Math.round(scrap.getCount() * inputCount * ratio));

                ItemStack scaledScrap = scrap.copy();
                scaledScrap.setCount(amount);

                String key = ItemKeyUtil.fromItemStack(scaledScrap);

                if (!resultsMap.containsKey(key)) {
                    resultsMap.put(key, scaledScrap);
                } else {
                    ItemStack existing = resultsMap.get(key);
                    existing.grow(scaledScrap.getCount());
                }
            }
            selectedItems.remove(input);
        }

        scrappedResults = new ArrayList<>(resultsMap.values());
    }


    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        super.clicked(slotId, dragType, clickType, player);
        if (!getSlot(slotId).mayPickup(player)) return;

        List<ItemStack> stacks = new ArrayList<>();
        for (int slotIndex : USEABLE_SLOTS) {
            ItemStack itemInSlot = getSlot(slotIndex).getItem();
            if (itemInSlot.isEmpty()) continue;
            stacks.add(itemInSlot);
        }
        selectedItems = stacks;
    }

    @Override
    protected void onMenuClosed(Player player) {
        for (ItemStack itemStack : selectedItems) {
            player.getInventory().placeItemBackInInventory(itemStack);
        }
    }
}
