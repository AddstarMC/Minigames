package au.com.mineauz.minigames.backend.sqlite;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.backend.Backend;
import au.com.mineauz.minigames.backend.BackendImportCallback;
import au.com.mineauz.minigames.backend.ConnectionHandler;
import au.com.mineauz.minigames.backend.ConnectionPool;
import au.com.mineauz.minigames.backend.ExportNotifier;
import au.com.mineauz.minigames.backend.StatementKey;
import au.com.mineauz.minigames.backend.both.SQLExport;
import au.com.mineauz.minigames.backend.both.SQLImport;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import au.com.mineauz.minigames.stats.StoredStat;

public class SQLiteBackend extends Backend {
	private ConnectionPool pool;
	private final Logger logger;
	
	private StatementKey insertMinigame;
	private StatementKey getMinigameId;
	
	private StatementKey insertPlayer;
	
	private SQLiteStatLoader loader;
	private SQLiteStatSaver saver;
	
	public SQLiteBackend(Logger logger) {
		this.logger = logger;
		
		loader = new SQLiteStatLoader(this, logger);
		saver = new SQLiteStatSaver(this, logger);
	}
	@Override
	public boolean initialize(ConfigurationSection config) {
		try {
			Class.forName("org.sqlite.JDBC");
			
			// Create the pool
			File path = new File(Minigames.plugin.getDataFolder(), "minigames.db");
			
			String url = String.format("jdbc:sqlite:" + path.getAbsolutePath());
			
			pool = new ConnectionPool(url, null, null);
			
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
			logger.severe("Failed to find MySQL JDBC driver. This version of craftbukkit is defective.");
		}
		
		return false;
	}
	
	@Override
	public void shutdown() {
		pool.closeConnections();
	}
	
	private void ensureTables(ConnectionHandler connection) throws SQLException {
		Statement statement = connection.getConnection().createStatement();
		try {
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
				statement.executeUpdate("CREATE TABLE `Minigames` (`minigame_id` INTEGER PRIMARY KEY ASC, `name` TEXT UNIQUE);");
				statement.executeUpdate("CREATE INDEX `Minigames_NameLookup` ON `Minigames` (`name`, `minigame_id`);");
			}
			
			// Check the player stats table
			try {
				statement.executeQuery("SELECT 1 FROM `PlayerStats` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `PlayerStats` (`player_id` TEXT REFERENCES `Players` (`player_id`) ON DELETE CASCADE, `minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `stat` TEXT NOT NULL, `value` INTEGER, PRIMARY KEY (`player_id`, `minigame_id`, `stat`));");
			}
			
			// Check the stat metadata table
			try {
				statement.executeQuery("SELECT 1 FROM `StatMetadata` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `StatMetadata` (`minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `stat` TEXT NOT NULL, `display_name` TEXT, `format` TEXT, PRIMARY KEY (`minigame_id`, `stat`));");
			}
		} finally {
			statement.close();
		}
	}
	
	private void createStatements() {
		insertMinigame = new StatementKey("INSERT OR IGNORE INTO `Minigames` (`name`) VALUES (?);", true);
		getMinigameId = new StatementKey("SELECT `minigame_id` FROM `Minigames` WHERE `name` = ?;");
		insertPlayer = new StatementKey("INSERT OR REPLACE INTO `Players` VALUES (?, ?, ?);");
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
				return rs.getInt("minigame_id");
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
	protected BackendImportCallback getImportCallback() {
		return new SQLImport(pool);
	}
	
	@Override
	public void exportTo(Backend other, ExportNotifier notifier) {
		BackendImportCallback callback = getImportCallback(other);
		SQLExport exporter = new SQLExport(pool, callback, notifier);
		exporter.beginExport();
	}
}
