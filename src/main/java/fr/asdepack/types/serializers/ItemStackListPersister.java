package fr.asdepack.types.serializers;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType; // Or LongStringType for big lists
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class ItemStackListPersister extends StringType {

    @Getter
    private static final ItemStackListPersister singleton = new ItemStackListPersister();

    protected ItemStackListPersister() {
        super(SqlType.STRING, new Class<?>[] { List.class });
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if(javaObject == null) return null;
        if(!(javaObject instanceof List)) return null;
        if(!(((List<?>) javaObject).isEmpty() || ((List<?>) javaObject).get(0) instanceof ItemStack)) return null;
        List<ItemStack> list = (List<ItemStack>) javaObject;

        ListTag nbtList = new ListTag();
        for (ItemStack stack : list) {
            if (stack != null && !stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                stack.save(itemTag);
                nbtList.add(itemTag);
            }
        }

        CompoundTag root = new CompoundTag();
        root.put("Items", nbtList);
        return root.toString();
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        String nbtString = (String) sqlArg;
        List<ItemStack> resultList = new ArrayList<>();

        if (nbtString == null || nbtString.isEmpty()) {
            return resultList;
        }

        try {
            CompoundTag root = TagParser.parseTag(nbtString);
            ListTag nbtList = root.getList("Items", 10);

            for (Tag t : nbtList) {
                resultList.add(ItemStack.of((CompoundTag) t));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
