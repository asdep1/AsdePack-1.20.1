package fr.asdepack.server.database;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.asdepack.server.KitManager;
import fr.asdepack.server.Server;
import fr.asdepack.types.Kit;
import lombok.Getter;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class DatabaseManager {
    public ConnectionSource connectionSource;
    @Getter
    private KitManager kitManager;

    public DatabaseManager(String databasePath) throws SQLException {
        Server.getLogger().log(new LogRecord(Level.INFO, "Connecting to database at " + databasePath));
        String databaseUrl = "jdbc:sqlite:" + databasePath;
        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);

        this.connectionSource = connectionSource;
        this.kitManager = new KitManager(DaoManager.createDao(connectionSource, Kit.class));
        TableUtils.createTableIfNotExists(connectionSource, Kit.class);

    }
}
