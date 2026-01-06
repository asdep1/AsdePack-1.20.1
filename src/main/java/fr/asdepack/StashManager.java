package fr.asdepack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.world.item.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StashManager {
    private final Gson GSON = new Gson();
    private final Connection connection;

    public StashManager(String databaseFilePath) throws SQLException {

        String url = "jdbc:sqlite:" + databaseFilePath;
        this.connection = DriverManager.getConnection(url);
        initDatabase();
    }

    private void initDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS player_stash (" +
                            "uuid TEXT PRIMARY KEY," +
                            "items TEXT NOT NULL" +
                            ")"
            );
        }
    }

    public List<ItemStack> getStash(UUID playerUUID) {
        List<ItemStack> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT items FROM player_stash WHERE uuid = ?"
        )) {
            ps.setString(1, playerUUID.toString());
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

    public void saveStash(UUID playerUUID, List<ItemStack> items) {
        JsonArray array = new JsonArray();

        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                array.add(ItemStackJson.toJson(stack));
            }
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO player_stash(uuid, items) VALUES (?, ?)"
        )) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, GSON.toJson(array));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
