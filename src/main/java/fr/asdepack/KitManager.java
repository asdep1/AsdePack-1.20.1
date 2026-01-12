package fr.asdepack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.world.item.ItemStack;

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
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS kits (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT UNIQUE NOT NULL,
                            icon TEXT NOT NULL,
                            items TEXT NOT NULL,
                            cost INTEGER NOT NULL,
                            permission TEXT,
                            cooldown INTEGER NOT NULL
                        )
                    """);
        }
    }

    public Kit getKit(String name) {
        try (PreparedStatement ps = connection.prepareStatement("""
                    SELECT * FROM kits WHERE name = ?
                """)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return fromResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Kit> getAllKits() {
        List<Kit> kits = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("""
                    SELECT * FROM kits ORDER BY id ASC
                """)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                kits.add(fromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kits;
    }

    public boolean saveKit(Kit kit) {
        try (PreparedStatement ps = connection.prepareStatement("""
                    INSERT OR REPLACE INTO kits(name, icon, items, cost, permission, cooldown)
                    VALUES (?, ?, ?, ?, ?, ?)
                """)) {

            ps.setString(1, kit.getName());
            ps.setString(2, GSON.toJson(ItemStackJson.toJson(kit.getIcon())));
            ps.setString(3, serializeItems(kit.getItems()));
            ps.setInt(4, kit.getCost());
            ps.setString(5, kit.getPermission());
            ps.setInt(6, kit.getCooldown());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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

    private Kit fromResultSet(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        ItemStack icon = ItemStackJson.fromJson(
                JsonParser.parseString(rs.getString("icon")).getAsJsonObject()
        );
        List<ItemStack> items = deserializeItems(rs.getString("items"));
        int cost = rs.getInt("cost");
        String permission = rs.getString("permission");
        int cooldown = rs.getInt("cooldown");

        return new Kit(name, items, icon, cost, permission, cooldown);
    }

    private String serializeItems(List<ItemStack> items) {
        JsonArray array = new JsonArray();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                array.add(ItemStackJson.toJson(stack));
            }
        }
        return GSON.toJson(array);
    }

    private List<ItemStack> deserializeItems(String json) {
        List<ItemStack> items = new ArrayList<>();
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();

        array.forEach(el ->
                items.add(ItemStackJson.fromJson(el.getAsJsonObject()))
        );

        return items;
    }
}
