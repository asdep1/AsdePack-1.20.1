package fr.asdepack.types.serializers;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ItemStackPersister extends StringType {
    @Getter
    private static final ItemStackPersister singleton = new ItemStackPersister();

    private static final Set<String> ALLOWED_TACZ_KEYS = new HashSet<>();

    static {
        ALLOWED_TACZ_KEYS.add("GunId");
        ALLOWED_TACZ_KEYS.add("AttachmentId");
        ALLOWED_TACZ_KEYS.add("AmmoId");
        ALLOWED_TACZ_KEYS.add("BlockId");
    }

    protected ItemStackPersister() {
        super(SqlType.STRING, new Class<?>[] { ItemStack.class });
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        ItemStack stack = (ItemStack) javaObject;
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        CompoundTag tag = new CompoundTag();
        stack.save(tag);

        if (tag.contains("taczitemtoscrap")) { //permit tacz item to correctly be get for the scrap system
            tag.getCompound("tag").getAllKeys().removeIf(key -> !ALLOWED_TACZ_KEYS.contains(key));
        }
        if (tag.contains("tag")) {
            if (tag.getCompound("tag").isEmpty()) {
                tag.remove("tag");
            }
        }
        return tag.toString();
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        String nbtString = (String) sqlArg;
        if (nbtString == null || nbtString.isEmpty()) {
            return ItemStack.EMPTY;
        }
        try {
            CompoundTag tag = TagParser.parseTag(nbtString);
            if (tag.contains("tag") && tag.getCompound("tag").isEmpty()) {
                tag.remove("tag");
            }
            return ItemStack.of(tag);
        } catch (Exception e) {
            e.printStackTrace();
            return ItemStack.EMPTY;
        }
    }
}