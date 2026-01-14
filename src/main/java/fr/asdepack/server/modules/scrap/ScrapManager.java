package fr.asdepack.server.modules.scrap;

import com.j256.ormlite.dao.Dao;
import fr.asdepack.server.Server;
import fr.asdepack.types.Scrap;
import lombok.AllArgsConstructor;
import net.minecraft.world.item.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ScrapManager {
    public Dao<Scrap, String> scrapDao;

    public List<Scrap> getScraps() throws SQLException {
        return this.scrapDao.queryForAll();
    }

    public Scrap getScrapByItem(ItemStack item) {
        List<Scrap> scraps = new ArrayList<>();
        try {
            scraps = this.scrapDao.queryForEq("item", item);
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
        if (!scraps.isEmpty()) {
            return scraps.get(0);
        }
        return null;
    }

   public boolean addScrap(Scrap scrap) throws SQLException {
        if (scrap == null || scrap.getItem() == ItemStack.EMPTY) {
            return false;
        }
        if (scrap.getScraps() == null) {
            scrap.setScraps(new ArrayList<>());
        }
        if (getScrapByItem(scrap.getItem()) != null) {
            throw new SQLException("Scrap entry with item " + scrap.getItem() + " already exists.");
        }
        scrap.getItem().setCount(1);
        int result = this.scrapDao.createOrUpdate(scrap).getNumLinesChanged();
        return result > 0;
   }

    public void removeScrap(ItemStack item) {
        try {
            Scrap scrap = getScrapByItem(item);
            if (scrap != null) {
                this.scrapDao.delete(scrap);
            }
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
    }

    public void updateScrap(Scrap scrap) {
        try {
            this.scrapDao.update(scrap);
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
    }
}