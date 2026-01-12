package fr.asdepack;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemStackJson {

    public static JsonObject toJson(ItemStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        obj.addProperty("count", stack.getCount());

        if (stack.hasTag()) {
            obj.addProperty("nbt", stack.getTag().toString());
        }

        return obj;
    }

    public static ItemStack fromJson(JsonObject obj) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(obj.get("id").getAsString()));
        int count = obj.get("count").getAsInt();

        ItemStack stack = new ItemStack(item, count);

        if (obj.has("nbt")) {
            try {
                CompoundTag tag = TagParser.parseTag(obj.get("nbt").getAsString());
                stack.setTag(tag);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }

        return stack;
    }
}
