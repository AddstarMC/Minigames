package au.com.mineauz.minigames.backend.mysql;

import java.sql.SQLException;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.backend.ConnectionHandler;
import au.com.mineauz.minigames.backend.StatementKey;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatFormat;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;

class MySQLStatSaver {
	private final MySQLBackend backend;
	private final Logger logger;
	
	private final StatementKey insertStat;
	private final StatementKey insertStatTotal;
	private final StatementKey insertStatMin;
	private final StatementKey insertStatMax;
	
	private final StatementKey[] insertStatements = new StatementKey[4];
	
	public MySQLStatSaver(MySQLBackend backend, Logger logger) {
		this.backend = backend;
		this.logger = logger;
		
		// Create statements
		insertStat = new StatementKey("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=VALUES(`value`)");
		insertStatTotal = new StatementKey("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=`value`+VALUES(`value`)");
		insertStatMin = new StatementKey("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=LEAST(`value`, VALUES(`value`))");
		insertStatMax = new StatementKey("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=GREATEST(`value`, VALUES(`value`))");
		
		// Prepare lookup table
		insertStatements[StatValueField.Last.ordinal()] = insertStat;
		insertStatements[StatValueField.Min.ordinal()] = insertStatMin;
		insertStatements[StatValueField.Max.ordinal()] = insertStatMax;
		insertStatements[StatValueField.Total.ordinal()] = insertStatTotal;
	}
	
	public void saveData(StoredGameStats data) {
		MinigameUtils.debugMessage("SQL Begining save of " + data);
		
		ConnectionHandler handler = null;
		try {
			handler = backend.getPool().getConnection();
			try {
				handler.beginTransaction();
				
				// Get the minigame id and update both the player and game
				int minigameId = backend.getMinigameId(handler, data.getMinigame());
				backend.updatePlayer(handler, data.getPlayer());
				
				saveStats(handler, data, data.getPlayer().getUUID(), minigameId);
				
				// Commit the changes
				handler.endTransaction();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Failed to save stats for " + data.getPlayer().getName(), e);
				
				handler.endTransactionFail();
			} finally {
				MinigameUtils.debugMessage("SQL Completed save of " + data);
			}
		} catch (SQLException e) { 
			e.printStackTrace();
		} finally {
			if (handler != null) {
				handler.release();
			}
		}
	}
	
	private void saveStats(ConnectionHandler handler, StoredGameStats data, UUID player, int minigameId) throws SQLException {
		// Prepare all updates
		for (Entry<MinigameStat, Long> entry : data.getStats().entrySet()) {
			StatFormat format = data.getFormat(entry.getKey());
			// Only store this stat if it's required
			if (entry.getKey().shouldStoreStat(entry.getValue(), format)) {
				queueStat(handler, entry.getKey(), entry.getValue(), format, player, minigameId);
			}
		}
		
		// Push all to database
		handler.executeBatch(insertStat);
		handler.executeBatch(insertStatTotal);
		handler.executeBatch(insertStatMin);
		handler.executeBatch(insertStatMax);
	}
	
	private void queueStat(ConnectionHandler handler, MinigameStat stat, long value, StatFormat format, UUID player, int minigameId) throws SQLException {
		for (StatValueField field : format.getFields()) {
			handler.batchUpdate(insertStatements[field.ordinal()], player.toString(), minigameId, stat.getName() + field.getSuffix(), value);
		}
	}
}
