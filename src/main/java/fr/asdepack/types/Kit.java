package fr.asdepack.types;

import com.google.gson.*;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.asdepack.Asdepack;
import fr.asdepack.server.modules.kits.KitCooldownManager;
import fr.asdepack.server.command.PermissionUtil;
import fr.asdepack.types.serializers.ItemStackListPersister;
import fr.asdepack.types.serializers.ItemStackPersister;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

@DatabaseTable(tableName = "kits")
@NoArgsConstructor
@AllArgsConstructor
public class Kit implements Serializable {
    @DatabaseField(generatedId = true)
    @Getter
    @Setter
    private int id;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private String name;
    @DatabaseField(canBeNull = false, persisterClass =  ItemStackPersister.class)
    @Getter
    @Setter
    private ItemStack icon;
    @DatabaseField(canBeNull = false, persisterClass =  ItemStackListPersister.class)
    @Getter
    @Setter
    private List<ItemStack> items;
    @Getter
    @Setter
    @DatabaseField(canBeNull = false)
    private int cost;
    @Getter
    @Setter
    @DatabaseField(canBeNull = false)
    private String permission;
    @Getter
    @Setter
    @DatabaseField(canBeNull = false)
    private int cooldown;

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static RETURN_CODE canGive(Kit kit, ServerPlayer player) {
        if (kit.permission != null &&
                !PermissionUtil.hasPermission(player, kit.permission)) {
            return RETURN_CODE.NO_PERMISSION;
        }

        if (KitCooldownManager.isOnCooldown(player, kit)) {
            return RETURN_CODE.ON_COOLDOWN;
        }

        if (Asdepack.VAULT_ADAPTER.isEnabled() && Asdepack.VAULT_ADAPTER.getBalance(player) < kit.cost) {
            return RETURN_CODE.INSUFFICIENT_FUNDS;
        }

        return RETURN_CODE.SUCCESS;
    }

    public enum RETURN_CODE {
        SUCCESS,
        NO_PERMISSION,
        ON_COOLDOWN,
        INSUFFICIENT_FUNDS
    }

    public static class Serializer implements JsonDeserializer<Kit>, JsonSerializer<Kit> {

        // Use a dedicated Gson instance for Kit serialization
        private static final Gson GSON = new GsonBuilder()
                .registerTypeAdapter(Kit.class, new Serializer())
                .disableHtmlEscaping() // Essential for NBT strings containing quotes
                .create();

        @Override
        public Kit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Basic Fields with safety checks
            int id = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : 0;
            String name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : "Unnamed Kit";
            int cost = jsonObject.has("cost") ? jsonObject.get("cost").getAsInt() : 0;
            String permission = jsonObject.has("permission") ? jsonObject.get("permission").getAsString() : "";
            int cooldown = jsonObject.has("cooldown") ? jsonObject.get("cooldown").getAsInt() : 0;

            // 1. Deserialize Icon (NBT String -> ItemStack)
            ItemStack icon = ItemStack.EMPTY;
            if (jsonObject.has("icon")) {
                try {
                    String iconNbt = jsonObject.get("icon").getAsString();
                    icon = ItemStack.of(TagParser.parseTag(iconNbt));
                } catch (Exception e) {
                    System.err.println("Failed to parse icon for kit " + name);
                    e.printStackTrace();
                }
            }

            // 2. Deserialize Items List (JsonArray of Strings -> List<ItemStack>)
            List<ItemStack> items = new java.util.ArrayList<>();
            if (jsonObject.has("items")) {
                JsonArray itemsArray = jsonObject.getAsJsonArray("items");
                for (JsonElement element : itemsArray) {
                    try {
                        String itemNbt = element.getAsString();
                        ItemStack stack = ItemStack.of(TagParser.parseTag(itemNbt));
                        if (!stack.isEmpty()) {
                            items.add(stack);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to parse an item in kit " + name);
                        e.printStackTrace();
                    }
                }
            }

            return new Kit(id, name, icon, items, cost, permission, cooldown);
        }

        @Override
        public JsonElement serialize(Kit src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            // Basic Fields
            jsonObject.addProperty("id", src.getId());
            jsonObject.addProperty("name", src.getName());
            jsonObject.addProperty("cost", src.getCost());
            jsonObject.addProperty("permission", src.getPermission());
            jsonObject.addProperty("cooldown", src.getCooldown());

            // 1. Serialize Icon
            if (src.getIcon() != null && !src.getIcon().isEmpty()) {
                CompoundTag tag = new CompoundTag();
                src.getIcon().save(tag);
                // Store as a direct string property
                jsonObject.addProperty("icon", tag.toString());
            }

            // 2. Serialize Items
            JsonArray itemsArray = new JsonArray();
            if (src.getItems() != null) {
                for (ItemStack stack : src.getItems()) {
                    if (stack != null && !stack.isEmpty()) {
                        CompoundTag itemTag = new CompoundTag();
                        stack.save(itemTag);
                        // Add the NBT string directly to the array
                        itemsArray.add(itemTag.toString());
                    }
                }
            }
            jsonObject.add("items", itemsArray);

            return jsonObject;
        }

        // Static Helpers
        @Nullable
        public static Kit fromJson(String pJson) {
            return GsonHelper.fromNullableJson(GSON, pJson, Kit.class, false);
        }

        public static String toJson(Kit pKit) {
            return GSON.toJson(pKit);
        }
    }
}
