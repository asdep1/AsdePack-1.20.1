package fr.asdepack.server.modules.kits;

import com.j256.ormlite.dao.Dao;
import fr.asdepack.server.Server;
import fr.asdepack.types.Kit;
import lombok.AllArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class KitManager {
    public Dao<Kit, String> kitDao;

    public List<Kit> getKits() throws SQLException {
        return this.kitDao.queryForAll();
    }

    public ItemStack getKitIcon(String name) throws SQLException {
        List<Kit> result = this.kitDao.query(
                this.kitDao.queryBuilder().selectColumns("icon").where().eq("name", name).prepare()
        );
        if (result == null) {
            return new ItemStack(Items.BARRIER);
        }
        return result.get(0).getIcon();
    }

    public Kit getKitByName(String name) {
        List<Kit> kits = new ArrayList<>();
        try {
            kits = this.kitDao.queryForEq("name", name);
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
        if (!kits.isEmpty()) {
            return kits.get(0);
        }
        return null;
    }

    public boolean saveKit(Kit kit) throws SQLException {
        if (kit.getIcon() == null) {
            kit.setIcon(new ItemStack(Items.BARRIER));
        }
        if (getKitByName(kit.getName()) != null) {
            throw new SQLException("Kit with name " + kit.getName() + " already exists.");
        }
        int result = this.kitDao.createOrUpdate(kit).getNumLinesChanged();
        return result > 0;
    }

    public void removeKit(String name) {
        try {
            Kit kit = getKitByName(name);
            if (kit != null) {
                this.kitDao.delete(kit);
            }
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
    }

    public void setPermission(String name, String permission) {
        try {
            Kit kit = getKitByName(name);
            if (kit == null) {
                throw new SQLException("Kit with name " + name + " does not exist.");
            }
            kit.setPermission(permission);
            this.kitDao.update(kit);
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
    }

    public void setCooldown(String name, int cooldown) {
        try {
            Kit kit = getKitByName(name);
            if (kit == null) {
                throw new SQLException("Kit with name " + name + " does not exist.");
            }
            kit.setCooldown(cooldown);
            this.kitDao.update(kit);
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
    }

    public void setCost(String name, int cost) {
        try {
            Kit kit = getKitByName(name);
            if (kit == null) {
                throw new SQLException("Kit with name " + name + " does not exist.");
            }
            kit.setCost(cost);
            this.kitDao.update(kit);
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
    }

    public void setIcon(String name, ItemStack icon) {
        try {
            Kit kit = getKitByName(name);
            if (kit == null) {
                throw new SQLException("Kit with name " + name + " does not exist.");
            }
            kit.setIcon(icon);
            this.kitDao.update(kit);
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
    }
}