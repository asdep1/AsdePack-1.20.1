package fr.asdepack.server;

import fr.asdepack.server.database.DatabaseManager;
import lombok.Getter;

import java.sql.SQLException;
import java.util.logging.Logger;

public class Server {
    @Getter
    private static Logger logger;
    @Getter
    private static DatabaseManager databaseManager;

    public static void init() throws SQLException {
        logger = Logger.getLogger("AsdepackServer"); // Initialize the logger before db manager
        databaseManager = new DatabaseManager("config/server.db");
    }
}
