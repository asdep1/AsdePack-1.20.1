package fr.asdepack.server.modules.scrap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fr.asdepack.helpers.ItemKeyUtil;
import fr.asdepack.helpers.ItemStackJson;
import net.minecraft.world.item.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScrapManager {
    private final Gson GSON = new Gson();
    private final Connection connection;

    public ScrapManager(String databaseFilePath) throws SQLException {

        String url = "jdbc:sqlite:" + databaseFilePath;
        this.connection = DriverManager.getConnection(url);
        initDatabase();
    }

    private void initDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS itemscrap (" +
                            "item_key TEXT PRIMARY KEY," +
                            "scraps TEXT NOT NULL" +
                            ")"
            );
        }
    }

    public List<ItemStack> getScrapList() {
        List<ItemStack> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT item_key FROM itemscrap"
        )) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String keyJson = rs.getString("item_key");

                ItemStack stack = ItemKeyUtil.toItemStack(keyJson);

                result.add(stack);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<ItemStack> getScrapFor(ItemStack input) {
        String key = ItemKeyUtil.fromItemStack(input);
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT scraps FROM itemscrap WHERE item_key = ?"
        )) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return List.of();

            JsonArray array = JsonParser.parseString(rs.getString(1)).getAsJsonArray();
            List<ItemStack> items = new ArrayList<>();

            for (JsonElement el : array) {
                items.add(ItemStackJson.fromJson(el.getAsJsonObject()));
            }

            return items;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void saveScrap(ItemStack input, List<ItemStack> scraps) {
        JsonArray array = new JsonArray();

        for (ItemStack stack : scraps) {
            if (!stack.isEmpty()) {
                array.add(ItemStackJson.toJson(stack));
            }
        }

        String key = ItemKeyUtil.fromItemStack(input);

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO itemscrap(item_key, scraps)  VALUES (?, ?)"
        )) {
            ps.setString(1, key);
            ps.setString(2, GSON.toJson(array));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeScrap(ItemStack input) {
        String key = ItemKeyUtil.fromItemStack(input);

        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM itemscrap WHERE item_key == ?"
        )) {
            ps.setString(1, key);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasScrap(ItemStack stack) {
        String key = ItemKeyUtil.fromItemStack(stack);

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 FROM itemscrap WHERE item_key = ? LIMIT 1"
        )) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
