package fr.asdepack.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

public class ItemStackPersister extends StringType {
    @Getter
    private static final ItemStackPersister singleton = new ItemStackPersister();

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
            return ItemStack.of(tag);
        } catch (Exception e) {
            e.printStackTrace();
            return ItemStack.EMPTY;
        }
    }
}