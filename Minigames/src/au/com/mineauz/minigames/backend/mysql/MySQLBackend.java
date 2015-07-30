package au.com.mineauz.minigames.backend.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.backend.Backend;
import au.com.mineauz.minigames.backend.ConnectionHandler;
import au.com.mineauz.minigames.backend.ConnectionPool;
import au.com.mineauz.minigames.backend.StatementKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import au.com.mineauz.minigames.stats.StoredStat;

public class MySQLBackend implements Backend {
	private ConnectionPool pool;
	private final Logger logger;
	
	private StatementKey insertMinigame;
	private StatementKey insertPlayer;
	
	private MySQLStatLoader loader;
	private MySQLStatSaver saver;
	
	public MySQLBackend(Logger logger) {
		this.logger = logger;
		
		loader = new MySQLStatLoader(this, logger);
		saver = new MySQLStatSaver(this, logger);
	}
	
	@Override
	public boolean initialize(ConfigurationSection config) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			// Create the pool
			String url = String.format("jdbc:mysql://%s/%s", 
					config.getString("host", "localhost:3306"), 
					config.getString("database", "database")
					);
			
			String username = config.getString("username", "username");
			String password = config.getString("password", "password");
			
			pool = new ConnectionPool(url, username, password);
			
			createStatements();
			
			// Test the connection
			try {
				ConnectionHandler handler = pool.getConnection();
				ensureTables(handler);
				handler.release();
				return true;
			} catch (SQLException e) {
				logger.severe("Failed to connect to the MySQL database. Please check your database settings");
			}
		} catch (ClassNotFoundException e) {
			logger.severe("Failed to find MySQL JDBC driver. This version of craftbukkit is defective.");
		}
		
		return false;
	}
	
	private void ensureTables(ConnectionHandler connection) throws SQLException {
		Statement statement = connection.getConnection().createStatement();
		try {
			// Check the players table
			try {
				statement.executeQuery("SELECT 1 FROM `Players` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `Players` (`player_id` CHAR(36) PRIMARY KEY, `name` VARCHAR(30) NOT NULL, `displayname` VARCHAR(30), INDEX (`name`, `player_id`));");
			}
			
			// Check the minigames table
			try {
				statement.executeQuery("SELECT 1 FROM `Minigames` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `Minigames` (`minigame_id` INTEGER AUTO_INCREMENT PRIMARY KEY, `name` VARCHAR(30) NOT NULL, UNIQUE INDEX (`name`));");
			}
			
			// Check the player stats table
			try {
				statement.executeQuery("SELECT 1 FROM `PlayerStats` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `PlayerStats` (`player_id` CHAR(36) REFERENCES `Players` (`player_id`) ON DELETE CASCADE, `minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `stat` VARCHAR(20) NOT NULL, `value` INTEGER, PRIMARY KEY (`player_id`, `minigame_id`, `stat`));");
			}
			
			// Check the stat metadata table
			try {
				statement.executeQuery("SELECT 1 FROM `StatMetadata` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `StatMetadata` (`minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `stat` VARCHAR(20) NOT NULL, `display_name` VARCHAR(20), `format` ENUM('LAST', 'LAST+TOTAL', 'MIN', 'MIN+TOTAL', 'MAX', 'MAX+TOTAL', 'MIN+MAX', 'MIN+MAX+TOTAL', 'TOTAL'), PRIMARY KEY (`minigame_id`, `stat`));");
			}
		} finally {
			statement.close();
		}
	}
	
	private void createStatements() {
		insertMinigame = new StatementKey("INSERT INTO `Minigames` (`name`) VALUES (?) ON DUPLICATE KEY UPDATE `minigame_id`=LAST_INSERT_ID(`minigame_id`);", true);
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
		ResultSet rs = handler.executeUpdateWithResults(insertMinigame, minigame.getName(false));
		
		try {
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				// Insert should always return the value
				throw new AssertionError();
			}
		} finally {
			rs.close();
		}
	}
	
	public void updatePlayer(ConnectionHandler handler, MinigamePlayer player) throws SQLException {
		handler.executeUpdate(insertPlayer, player.getUUID().toString(), player.getName(), player.getDisplayName());
	}
}
