package fr.asdepack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KitManager {
    private final Gson GSON = new Gson();
    private final Connection connection;

    public KitManager(String databaseFilePath) throws SQLException {

        String url = "jdbc:sqlite:" + databaseFilePath;
        this.connection = DriverManager.getConnection(url);
        initDatabase();
    }

    private void initDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS kits (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT UNIQUE NOT NULL," +
                            "icon TEXT NOT NULL," +
                            "kit TEXT NOT NULL" +
                            ")"
            );
        }
    }

    public List<String> getKitList() {
        List<String> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT name FROM kits ORDER BY id ASC"
        )) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public ItemStack getKitIcon(String name) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT icon FROM kits WHERE name = ?"
        )) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return new ItemStack(Items.CHEST);

            return ItemStackJson.fromJson(
                    JsonParser.parseString(rs.getString("icon")).getAsJsonObject()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Items.CHEST);
        }
    }

    public List<ItemStack> getKitFor(String name) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT kit FROM kits WHERE name = ?"
        )) {
            ps.setString(1, name);
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

    public boolean saveKit(String name, ItemStack icon, List<ItemStack> items) {
        JsonArray array = new JsonArray();

        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                array.add(ItemStackJson.toJson(stack));
            }
        }

        if (icon == null || icon.isEmpty()) {
            icon = new ItemStack(Items.CHEST);
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO kits(name, icon, kit)  VALUES (?, ?, ?)"
        )) {
            ps.setString(1, name);
            ps.setString(2, GSON.toJson(ItemStackJson.toJson(icon)));
            ps.setString(3, GSON.toJson(array));
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removeKit(String name) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM kits WHERE name == ?"
        )) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
