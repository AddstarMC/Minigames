package au.com.mineauz.minigames.backend.sqlite;

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

class SQLiteStatSaver {
	private final SQLiteBackend backend;
	private final Logger logger;
	
	private final StatementKey insertStat;
	private final StatementKey insertStatTotal;
	private final StatementKey insertStatMin;
	private final StatementKey insertStatMax;
	
	private final StatementKey[] insertStatements = new StatementKey[4];
	
	public SQLiteStatSaver(SQLiteBackend backend, Logger logger) {
		this.backend = backend;
		this.logger = logger;
		
		// Create statements
		insertStat = new StatementKey("INSERT OR REPLACE INTO `PlayerStats` VALUES (?, ?, ?, ?);");
		insertStatTotal = new StatementKey("INSERT OR REPLACE INTO `PlayerStats` VALUES (?, ?, ?, (SELECT coalesce((SELECT (`value`+?) FROM `PlayerStats` WHERE `player_id`=? AND `minigame_id`=? AND `stat`=?), ?)));");
		insertStatMin = new StatementKey("INSERT OR REPLACE INTO `PlayerStats` VALUES (?, ?, ?, (SELECT coalesce((SELECT MIN(`value`,?) FROM `PlayerStats` WHERE `player_id`=? AND `minigame_id`=? AND `stat`=?), ?)));");
		insertStatMax = new StatementKey("INSERT OR REPLACE INTO `PlayerStats` VALUES (?, ?, ?, (SELECT coalesce((SELECT MAX(`value`,?) FROM `PlayerStats` WHERE `player_id`=? AND `minigame_id`=? AND `stat`=?), ?)));");
		
		// Prepare lookup table
		insertStatements[StatValueField.Last.ordinal()] = insertStat;
		insertStatements[StatValueField.Min.ordinal()] = insertStatMin;
		insertStatements[StatValueField.Max.ordinal()] = insertStatMax;
		insertStatements[StatValueField.Total.ordinal()] = insertStatTotal;
	}
	
	public void saveData(StoredGameStats data) {
		MinigameUtils.debugMessage("SQL Begining save of " + data);
		
		try {
			ConnectionHandler handler = backend.getPool().getConnection();
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
			if (field == StatValueField.Last) {
				handler.batchUpdate(insertStat, player.toString(), minigameId, stat.getName() + field.getSuffix(), value);
			} else {
				handler.batchUpdate(insertStatements[field.ordinal()], player.toString(), minigameId, stat.getName() + field.getSuffix(), value, player.toString(), minigameId, stat.getName() + field.getSuffix(), value);
			}
		}
	}
}
