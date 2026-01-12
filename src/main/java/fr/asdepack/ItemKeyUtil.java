package fr.asdepack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemKeyUtil {

    private static final Gson GSON = new Gson();

    public static String fromItemStack(ItemStack stack) {
        JsonObject key = new JsonObject();

        key.addProperty(
                "item",
                BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
        );

        // TACZ compat
        if (stack.hasTag()) {
            if (stack.getTag().contains("GunId")) {
                key.addProperty("gunId", stack.getTag().getString("GunId"));
            }
            if (stack.getTag().contains("AttachmentId")) {
                key.addProperty("attachmentId", stack.getTag().getString("AttachmentId"));
            }
            if (stack.getTag().contains("AmmoId")) {
                key.addProperty("ammoId", stack.getTag().getString("AmmoId"));
            }
            if (stack.getTag().contains("BlockId")) {
                key.addProperty("blockId", stack.getTag().getString("BlockId"));
            }
        }


        return GSON.toJson(key);
    }

    public static ItemStack toItemStack(String keyJson) {
        JsonObject key = JsonParser.parseString(keyJson).getAsJsonObject();

        ResourceLocation itemId = ResourceLocation.parse(
                key.get("item").getAsString()
        );

        Item item = BuiltInRegistries.ITEM.get(itemId);
        ItemStack stack = new ItemStack(item);

        // TACZ compat
        if (key.has("gunId")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("GunId", key.get("gunId").getAsString());
            stack.setTag(tag);
        }
        if (key.has("attachmentId")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("AttachmentId", key.get("attachmentId").getAsString());
            stack.setTag(tag);
        }
        if (key.has("ammoId")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("AmmoId", key.get("ammoId").getAsString());
            stack.setTag(tag);
        }
        if (key.has("blockId")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("BlockId", key.get("blockId").getAsString());
            stack.setTag(tag);
        }

        return stack;
    }
}
