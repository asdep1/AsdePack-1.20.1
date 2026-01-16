package fr.asdepack.types;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.asdepack.types.serializers.ItemStackListPersister;
import fr.asdepack.types.serializers.ItemStackPersister;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@DatabaseTable(tableName = "scraps")
@NoArgsConstructor
@AllArgsConstructor
public class Scrap {
    @DatabaseField(generatedId = true)
    @Getter
    @Setter
    private int id;
    @DatabaseField(canBeNull = false, persisterClass =  ItemStackPersister.class)
    @Getter
    @Setter
    private ItemStack item;
    @DatabaseField(canBeNull = false, persisterClass =  ItemStackListPersister.class)
    @Getter
    @Setter
    private List<ItemStack> scraps;

    public static ItemStack compatTacz(ItemStack stack) throws Exception {  //permit tacz item to correctly be get for the scrap system
        ResourceLocation itemId = stack.getItem().builtInRegistryHolder().key().location();

        if (!itemId.getNamespace().equals("tacz")) {
            stack.setCount(1);
            return stack;
        }
        ResourceLocation relatedResourceLocation = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if(relatedResourceLocation == null) {
            throw new Exception("ResourceLocation is null");
        }
        Item relatedItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(relatedResourceLocation.toString()));
        if(relatedItem == null) {
            throw new Exception("Related item is null");
        }
        ItemStack tacz = new ItemStack(relatedItem, 1);
        CompoundTag key = stack.getOrCreateTag();
        // TACZ compat
        String[] taczKeys = {"GunId", "AttachmentId", "AmmoId", "BlockId"};
        for (String taczKey : taczKeys) {
            if (key.contains(taczKey)) {
                CompoundTag tag = new CompoundTag();
                tag.putString(taczKey, key.getString(taczKey));
                tag.putBoolean("taczitemtoscrap", true);
                tacz.setTag(tag);
                break;
            }
        }
        return tacz;
    }

    @Override
    public String toString() {
        return "Scrap{" +
                "id=" + id +
                ", item=" + item +
                ", scraps=" + scraps +
                '}';
    }
}
