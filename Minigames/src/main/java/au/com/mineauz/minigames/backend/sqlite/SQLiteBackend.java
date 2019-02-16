package au.com.mineauz.minigames.backend.sqlite;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.backend.*;
import au.com.mineauz.minigames.backend.both.SQLExport;
import au.com.mineauz.minigames.backend.both.SQLImport;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.*;
import com.google.common.collect.Maps;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("SyntaxError")
public class SQLiteBackend extends Backend {
    private final Logger logger;
    private ConnectionPool pool;
    private StatementKey insertMinigame;
    private StatementKey getMinigameId;

    private StatementKey insertPlayer;
    private StatementKey loadStatSettings;
    private StatementKey saveStatSettings;
    private boolean debug;
    private SQLiteStatLoader loader;
    private SQLiteStatSaver saver;
    private File database;


    public SQLiteBackend(Logger logger) {
        this.logger = logger;
        loader = new SQLiteStatLoader(this, logger);
        saver = new SQLiteStatSaver(this, logger);
        try {
            database = new File(Minigames.getPlugin().getDataFolder(), "minigames.db");
        } catch (NullPointerException e) {
            logger.warning("Could not locate or set database path");
        }

    }

    public void setDatabase(File dbbath) {
        database = dbbath;
    }


    @Override
    public boolean initialize(ConfigurationSection config, boolean debug) {
        this.debug = debug;

        try {
            Class.forName("org.sqlite.JDBC");

            // Create the pool
            if (database == null) {
                return false;
            }
            String url = "jdbc:sqlite:" + database.getAbsolutePath();
            if (debug) logger.info("URL: " + url);
            Properties properties = new Properties();
            properties.put("username", "");
            properties.put("password", "");
            if (debug) logger.info("Properties: " + properties.toString());
            pool = new ConnectionPool(url, properties);
            if (debug) logger.info("Pool: " + pool.toString());
            createStatements();

            // Test the connection
            try {
                ConnectionHandler handler = pool.getConnection();
                ensureTables(handler);
                handler.release();
                return true;
            } catch (SQLException e) {
                logger.severe("Failed to connect to the SQLite database. Please check your database settings");
            }
        } catch (ClassNotFoundException e) {
            logger.severe("Failed to find sqlite JDBC driver. This version of craftbukkit is defective.");
        }

        return false;
    }

    @Override
    public void shutdown() {
        pool.closeConnections();
    }

    @Override
    public void clean() {
        pool.removeExpired();
    }

    private boolean checkForColumn(Statement statement, String tableName, String columnName) {

        try {
            ResultSet rs = statement.executeQuery("Pragma table_info(`" + tableName + "`);");

            int nameColIndex = rs.findColumn("name");

            while (rs.next()) {
                if (rs.getString(nameColIndex).equals(columnName))
                    return true;
            }

            return false;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Exception looking for SQLite column " + columnName + " on table " + tableName, e);
            return false;
        }

    }

    private void ensureTables(ConnectionHandler connection) throws SQLException {
        try (Statement statement = connection.getConnection().createStatement()) {
            // Check the players table
            try {
                statement.executeQuery("SELECT 1 FROM `Players` LIMIT 0;");
            } catch (SQLException e) {
                statement.executeUpdate("CREATE TABLE `Players` (`player_id` TEXT PRIMARY KEY, `name` TEXT NOT NULL, `displayname` TEXT);");
                statement.executeUpdate("CREATE INDEX `Players_NameLookup` ON `Players` (`name`, `player_id`);");
            }

            // Check the minigames table
            try {
                statement.executeQuery("SELECT 1 FROM `Minigames` LIMIT 0;");
            } catch (SQLException e) {
                statement.executeUpdate("CREATE TABLE `Minigames` (`minigame_id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT UNIQUE);");
                statement.executeUpdate("CREATE INDEX `Minigames_NameLookup` ON `Minigames` (`name`, `minigame_id`);");
            }

            // Check the player stats table
            try {
                statement.executeQuery("SELECT 1 FROM `PlayerStats` LIMIT 0;");
            } catch (SQLException e) {
                statement.executeUpdate("CREATE TABLE `PlayerStats` (`player_id` TEXT REFERENCES `Players` (`player_id`) ON DELETE CASCADE, `minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `stat` TEXT NOT NULL, `value` INTEGER, `last_updated` datetime DEFAULT NULL, `entered` DATETIME default NULL, PRIMARY KEY (`player_id`, `minigame_id`, `stat`));");
            }

            // Check for column last_updated on the PlayerStats table
            try {
                if (!checkForColumn(statement, "PlayerStats", "last_updated")) {
                    logger.info("Adding SQLite column 'last_updated' to table PlayerStats");

                    statement.executeUpdate("ALTER TABLE `PlayerStats` ADD COLUMN `last_updated` DATETIME default NULL;");
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to add column last_updated to the PlayerStats table in the SQLite Minigames database", e);
            }

            // Check for column entered on the PlayerStats table
            try {
                if (!checkForColumn(statement, "PlayerStats", "entered")) {
                    logger.info("Adding SQLite column 'entered' to table PlayerStats");

                    statement.executeUpdate("ALTER TABLE `PlayerStats` ADD COLUMN `entered` DATETIME default NULL;");
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to add column entered to the PlayerStats table in the SQLite Minigames database", e);
            }

            // Check the stat metadata table
            try {
                statement.executeQuery("SELECT 1 FROM `StatMetadata` LIMIT 0;");
            } catch (SQLException e) {
                statement.executeUpdate("CREATE TABLE `StatMetadata` (`minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `stat` TEXT NOT NULL, `display_name` TEXT, `format` TEXT, PRIMARY KEY (`minigame_id`, `stat`));");
            }
        }
    }

    private void createStatements() {
        insertMinigame = new StatementKey("INSERT OR IGNORE INTO `Minigames` (`name`) VALUES (?);", true);
        getMinigameId = new StatementKey("SELECT `minigame_id` FROM `Minigames` WHERE `name` = ?;");
        insertPlayer = new StatementKey("INSERT OR REPLACE INTO `Players` VALUES (?, ?, ?);");
        loadStatSettings = new StatementKey("SELECT `stat`, `display_name`, `format` FROM `StatMetadata` WHERE `minigame_id`=?;");
        saveStatSettings = new StatementKey("INSERT OR REPLACE INTO `StatMetadata` VALUES (?, ?, ?, ?);");
    }

    ConnectionPool getPool() {
        return pool;
    }

    @Override
    public void saveGameStatus(StoredGameStats stats) {
        saver.saveData(stats);
    }

    @Override
    public List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order) {
        return loadStats(minigame, stat, field, order, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order, int offset, int length) {
        return loader.loadStatValues(minigame, stat, field, order, offset, length);
    }

    @Override
    public long getStat(Minigame minigame, UUID playerId, MinigameStat stat, StatValueField field) {
        return loader.loadSingleValue(minigame, stat, field, playerId);
    }

    public int getMinigameId(ConnectionHandler handler, Minigame minigame) throws SQLException {
        ResultSet rs = handler.executeQuery(getMinigameId, minigame.getName(false));
        try {
            if (rs.next()) {
                return rs.getInt("minigame_id");
            }
        } finally {
            rs.close();
        }

        rs = handler.executeUpdateWithResults(insertMinigame, minigame.getName(false));
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new AssertionError("Insert should have returned id");
            }
        } finally {
            rs.close();
        }
    }

    public void updatePlayer(ConnectionHandler handler, MinigamePlayer player) throws SQLException {
        handler.executeUpdate(insertPlayer, player.getUUID().toString(), player.getName(), player.getDisplayName());
    }

    @Override
    public Map<MinigameStat, StatSettings> loadStatSettings(Minigame minigame) {
        ConnectionHandler handler = null;
        try {
            handler = pool.getConnection();

            int minigameId = getMinigameId(handler, minigame);

            Map<MinigameStat, StatSettings> settings = Maps.newHashMap();

            try (ResultSet rs = handler.executeQuery(loadStatSettings, minigameId)) {
                while (rs.next()) {
                    String statName = rs.getString("stat");
                    String rawFormat = rs.getString("format");
                    String displayName = rs.getString("display_name");

                    MinigameStat stat = MinigameStats.getStat(statName);
                    if (stat == null) {
                        // Just ignore it
                        continue;
                    }

                    // Decode format
                    StatFormat format = null;
                    for (StatFormat f : StatFormat.values()) {
                        if (f.name().equalsIgnoreCase(rawFormat)) {
                            format = f;
                            break;
                        }
                    }

                    if (format == null) {
                        format = stat.getFormat();
                    }

                    StatSettings setting = new StatSettings(stat, format, displayName);
                    settings.put(stat, setting);
                }

                return settings;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        } finally {
            if (handler != null) {
                handler.release();
            }
        }
    }

    @Override
    public void saveStatSettings(Minigame minigame, Collection<StatSettings> settings) {
        ConnectionHandler handler = null;
        try {
            handler = pool.getConnection();
            handler.beginTransaction();

            int minigameId = getMinigameId(handler, minigame);
            for (StatSettings setting : settings) {
                handler.batchUpdate(saveStatSettings, minigameId, setting.getStat().getName(), setting.getDisplayName(), setting.getFormat().name().toUpperCase());
            }

            handler.executeBatch(saveStatSettings);
            handler.endTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            handler.endTransactionFail();
        } finally {
            if (handler != null) {
                handler.release();
            }
        }
    }

    @Override
    protected BackendImportCallback getImportCallback() {
        return new SQLImport(pool);
    }

    @Override
    public void exportTo(Backend other, ExportNotifier notifier) {
        BackendImportCallback callback = getImportCallback(other);
        SQLExport exporter = new SQLExport(pool, callback, notifier);
        exporter.beginExport();
    }

    @Override
    public boolean doConversion(ExportNotifier notifier) {
        BackendImportCallback callback = getImportCallback();
        FlatFileExporter exporter = new FlatFileExporter(new File(Minigames.getPlugin().getDataFolder(), "completion.yml"), callback, notifier);
        return exporter.doExport();
    }
}
