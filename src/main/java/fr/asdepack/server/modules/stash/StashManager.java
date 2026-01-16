package fr.asdepack.server.modules.stash;

import com.j256.ormlite.dao.Dao;
import fr.asdepack.server.Server;
import fr.asdepack.types.Stash;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StashManager {
    public Dao<Stash, String> stashDao;

    public Stash getStashByUUID(String uuid) {
        List<Stash> stash = new ArrayList<>();
        try {
            stash = this.stashDao.queryForEq("playeruuid", uuid);
        } catch (SQLException e) {
            Server.getLogger().warning(e.getMessage());
        }
        if (!stash.isEmpty()) {
            return stash.get(0);
        }
        return null;
    }

    public void addStash(Stash stash) throws SQLException {
        if (stash == null || stash.getPlayeruuid() == null) {
            return;
        }
        if (stash.getItems() == null) {
            stash.setItems(new ArrayList<>());
        }
        this.stashDao.createOrUpdate(stash);
    }
}