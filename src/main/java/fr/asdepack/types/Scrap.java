package fr.asdepack.types;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
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

    public static ItemStack compatTacz(ItemStack stack) {
        ItemStack tacz = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())));
        CompoundTag key = stack.getOrCreateTag();
        // TACZ compat
        if (key.contains("gunId")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("GunId", key.getString("gunId"));
            tacz.setTag(tag);
        }
        if (key.contains("attachmentId")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("AttachmentId", key.getString("attachmentId"));
            tacz.setTag(tag);
        }
        if (key.contains("ammoId")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("AmmoId", key.getString("ammoId"));
            tacz.setTag(tag);
        }
        if (key.contains("blockId")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("BlockId", key.getString("blockId"));
            tacz.setTag(tag);
        }
        if (tacz == null || tacz == ItemStack.EMPTY) {
            System.out.println("NNNNNNNNN");
            return ItemStack.EMPTY;
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
