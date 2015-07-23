package au.com.mineauz.minigames.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StoredStats;

public class SQLStatSaverTask implements Callable<Boolean> {
	private static final boolean defaultHasCompleted = true;
	
	private final SQLDatabase database;
	private final Logger logger;
	private final StoredStats data;
	
	private Connection con;
	
	public SQLStatSaverTask(Minigames plugin, StoredStats data) {
		this.database = plugin.getSQL();
		this.logger = plugin.getLogger();
		this.data = data;
	}
	
	@Override
	public Boolean call() throws Exception {
		con = getConnection();
		if (con == null) {
			return defaultHasCompleted;
		}
		
		return saveData(data);
	}
	
	private Connection getConnection() {
		if(!database.isOpen()) {
			if(!database.loadSQL()) {
				logger.warning("Database Connection was closed and could not be re-established!");
				return null;
			}
		}
		
		return database.getSql();
	}
	
	private boolean saveData(StoredStats data) {
		MinigameUtils.debugMessage("SQL Begining save of " + data);
		boolean hasCompleted = defaultHasCompleted;
		try {
			con.setAutoCommit(false);
			
			// Get the minigame id and update both the player and game
			int minigameId = SQLUtilities.getMinigameId(database.insertMinigame, data.getMinigame());
			SQLUtilities.updatePlayer(database.insertPlayer, data.getPlayer());
			
			// We will need this for rewards later
			hasCompleted = hasCompleted(data.getPlayer().getUUID(), minigameId);
			
			// First do the attempts table
			saveAttempt(data, minigameId);
			
			// Now the stats table
			saveStats(data, data.getPlayer().getUUID(), minigameId);
			
			// Commit the changes
			con.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Failed to save stats for " + data.getPlayer().getName(), e);
			
			try {
				con.rollback();
			} catch (SQLException ex) {
				// Ignore
			}
		} finally {
			MinigameUtils.debugMessage("SQL Completed save of " + data);
			try {
				con.setAutoCommit(true);
			} catch (SQLException ex) {
				// Ignore
			}
		}
		
		return hasCompleted;
	}
	
	// Check if the player has completed the minigame
	private boolean hasCompleted(UUID player, int minigameId) throws SQLException {
		PreparedStatement statement = database.checkCompletion;
		
		statement.setString(1, player.toString());
		statement.setInt(2, minigameId);
		
		ResultSet rs = statement.executeQuery();
		try {
			if (rs.next()) {
				return rs.getBoolean(1);
			} else {
				return false;
			}
		} finally {
			rs.close();
		}
	}
	
	private void saveAttempt(StoredStats data, int minigameId) throws SQLException {
		PreparedStatement statement = database.insertAttempt;
		
		statement.setString(1, data.getPlayer().getUUID().toString());
		statement.setInt(2, minigameId);
		long wins = data.getStat(MinigameStats.Wins);
		statement.setLong(3, wins);
		long attempts = data.getStat(MinigameStats.Attempts);
		statement.setLong(4, attempts);
		long time = data.getStat(MinigameStats.CompletionTime);
		statement.setLong(5, time);
		statement.setLong(6, time);
		statement.setLong(7, time);
		
		statement.setLong(8, wins);
		statement.setLong(9, attempts);
		statement.setLong(10, time);
		statement.setLong(11, time);
		statement.setLong(12, time);
		
		statement.executeUpdate();
	}
	
	private void saveStats(StoredStats data, UUID player, int minigameId) throws SQLException {
		// Prepare all updates
		for (Entry<MinigameStat, Long> entry : data.getStats().entrySet()) {
			// Only store this stat if it's required
			if (entry.getKey().shouldStoreStat(entry.getValue(), entry.getKey().getFormat())) {
				queueStat(entry.getKey(), entry.getValue(), player, minigameId);
			}
		}
		
		// Push all to database
		database.insertStat.executeBatch();
		database.insertStatTotal.executeBatch();
		database.insertStatMin.executeBatch();
		database.insertStatMax.executeBatch();
	}
	
	private void queueStat(MinigameStat stat, long value, UUID player, int minigameId) throws SQLException {
		switch (stat.getFormat()) {
		case Last:
			setStat(database.insertStat, player, minigameId, stat.getName(), value);
			break;
		case LastAndTotal:
			setStat(database.insertStat, player, minigameId, stat.getName(), value);
			setStat(database.insertStatTotal, player, minigameId, stat.getName() + "_total", value);
			break;
		case Max:
			setStat(database.insertStatMax, player, minigameId, stat.getName(), value);
			break;
		case MaxAndTotal:
			setStat(database.insertStatMax, player, minigameId, stat.getName(), value);
			setStat(database.insertStatTotal, player, minigameId, stat.getName() + "_total", value);
			break;
		case Min:
			setStat(database.insertStatMin, player, minigameId, stat.getName(), value);
			break;
		case MinAndTotal:
			setStat(database.insertStatMin, player, minigameId, stat.getName(), value);
			setStat(database.insertStatTotal, player, minigameId, stat.getName() + "_total", value);
			break;
		case MinMax:
			setStat(database.insertStatMin, player, minigameId, stat.getName() + "_min", value);
			setStat(database.insertStatMax, player, minigameId, stat.getName() + "_max", value);
			break;
		case MinMaxAndTotal:
			setStat(database.insertStatMin, player, minigameId, stat.getName() + "_min", value);
			setStat(database.insertStatMax, player, minigameId, stat.getName() + "_max", value);
			setStat(database.insertStatTotal, player, minigameId, stat.getName() + "_total", value);
			break;
		case Total:
			setStat(database.insertStatTotal, player, minigameId, stat.getName(), value);
			break;
		default:
			throw new AssertionError();
		}
	}
	
	// Applies the arguments to the prepared statement. Must be one of the stats prepared statements
	private void setStat(PreparedStatement statement, UUID id, int minigameId, String key, long value) throws SQLException {
		statement.setString(1, id.toString());
		statement.setInt(2, minigameId);
		statement.setString(3, key);
		statement.setLong(4, value);
		statement.setLong(5, value);
		
		statement.addBatch();
	}
}
