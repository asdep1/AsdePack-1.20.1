package fr.asdepack.types;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.asdepack.types.serializers.ItemStackListPersister;
import fr.asdepack.types.serializers.ItemStackPersister;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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

    public static ItemStack compatTacz(ItemStack stack) {  //permit tacz item to correctly be get for the scrap system
        ResourceLocation itemId = stack.getItem().builtInRegistryHolder().key().location();

        if (!itemId.getNamespace().equals("tacz")) {
            stack.setCount(1);
            return stack;
        }
        ItemStack tacz = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())));
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
