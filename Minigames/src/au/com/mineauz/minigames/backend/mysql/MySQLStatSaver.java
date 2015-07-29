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
import au.com.mineauz.minigames.stats.StoredGameStats;

class MySQLStatSaver {
	private final MySQLBackend backend;
	private final Logger logger;
	
	private final StatementKey insertStat;
	private final StatementKey insertStatTotal;
	private final StatementKey insertStatMin;
	private final StatementKey insertStatMax;
	
	public MySQLStatSaver(MySQLBackend backend, Logger logger) {
		this.backend = backend;
		this.logger = logger;
		
		// Create statements
		insertStat = new StatementKey("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=VALUES(`value`)");
		insertStatTotal = new StatementKey("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=`value`+VALUES(`value`)");
		insertStatMin = new StatementKey("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=LEAST(`value`, VALUES(`value`))");
		insertStatMax = new StatementKey("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=GREATEST(`value`, VALUES(`value`))");
	}
	
	public void saveData(StoredGameStats data) {
		MinigameUtils.debugMessage("SQL Begining save of " + data);
		
		try {
			ConnectionHandler handler = backend.getPool().getConnection();
			try {
				handler.beginTransaction();
				
				// Get the minigame id and update both the player and game
				int minigameId = backend.getMinigameId(data.getMinigame());
				backend.updatePlayer(data.getPlayer());
				
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
			// Only store this stat if it's required
			if (entry.getKey().shouldStoreStat(entry.getValue(), entry.getKey().getFormat())) {
				queueStat(handler, entry.getKey(), entry.getValue(), player, minigameId);
			}
		}
		
		// Push all to database
		handler.executeBatch(insertStat);
		handler.executeBatch(insertStatTotal);
		handler.executeBatch(insertStatMin);
		handler.executeBatch(insertStatMax);
	}
	
	private void queueStat(ConnectionHandler handler, MinigameStat stat, long value, UUID player, int minigameId) throws SQLException {
		switch (stat.getFormat()) {
		case Last:
			handler.batchUpdate(insertStat, player.toString(), minigameId, stat.getName(), value);
			break;
		case LastAndTotal:
			handler.batchUpdate(insertStat, player.toString(), minigameId, stat.getName(), value);
			handler.batchUpdate(insertStatTotal, player.toString(), minigameId, stat.getName() + "_total", value);
			break;
		case Max:
			handler.batchUpdate(insertStatMax, player.toString(), minigameId, stat.getName(), value);
			break;
		case MaxAndTotal:
			handler.batchUpdate(insertStatMax, player.toString(), minigameId, stat.getName(), value);
			handler.batchUpdate(insertStatTotal, player.toString(), minigameId, stat.getName() + "_total", value);
			break;
		case Min:
			handler.batchUpdate(insertStatMin, player.toString(), minigameId, stat.getName(), value);
			break;
		case MinAndTotal:
			handler.batchUpdate(insertStatMin, player.toString(), minigameId, stat.getName(), value);
			handler.batchUpdate(insertStatTotal, player.toString(), minigameId, stat.getName() + "_total", value);
			break;
		case MinMax:
			handler.batchUpdate(insertStatMin, player.toString(), minigameId, stat.getName() + "_min", value);
			handler.batchUpdate(insertStatMax, player.toString(), minigameId, stat.getName() + "_max", value);
			break;
		case MinMaxAndTotal:
			handler.batchUpdate(insertStatMin, player.toString(), minigameId, stat.getName() + "_min", value);
			handler.batchUpdate(insertStatMax, player.toString(), minigameId, stat.getName() + "_max", value);
			handler.batchUpdate(insertStatTotal, player.toString(), minigameId, stat.getName() + "_total", value);
			break;
		case Total:
			handler.batchUpdate(insertStatTotal, player.toString(), minigameId, stat.getName(), value);
			break;
		default:
			throw new AssertionError();
		}
	}
}
