package au.com.mineauz.minigames.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredStat;

public class SQLStatLoaderTask implements Callable<List<StoredStat>> {
	private final Minigame minigame;
	private final MinigameStat stat;
	private final StatValueField field;
	private final ScoreboardOrder order;
	
	private final int offset;
	private final int length;
	
	private final Logger logger;
	private final SQLDatabase database;
	
	private Connection con;
	
	public SQLStatLoaderTask(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order, int offset, int length, Minigames plugin) {
		this.minigame = minigame;
		this.stat = stat;
		this.field = field;
		this.order = order;
		this.offset = offset;
		this.length = length;
		
		this.database = plugin.getSQL();
		this.logger = plugin.getLogger();
	}
	
	@Override
	public List<StoredStat> call() throws Exception {
		con = getConnection();
		if (con == null) {
			return Collections.emptyList();
		}
		
		return loadMinigameData();
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
	
	private List<StoredStat> loadMinigameData() {
		MinigameUtils.debugMessage("SQL begining stat load for " + minigame.getName(false) + ", " + stat + ", " + field);
		try {
			// First get the id
			int minigameId = SQLUtilities.getMinigameId(database.insertMinigame, minigame);
			
			if (stat == MinigameStats.Wins || stat == MinigameStats.Attempts || stat == MinigameStats.CompletionTime) {
				return loadAttempts(minigameId);
			} else {
				return loadStats(minigameId);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Failed to load stats for " + minigame.getName(false), e);
			return Collections.emptyList();
		} finally {
			MinigameUtils.debugMessage("SQL completed stat load for " + minigame.getName(false));
		}
	}
	
	// Loads from the attempts table
	private List<StoredStat> loadAttempts(int minigameId) throws SQLException {
		String fieldName = stat.getName() + stat.getFormat().getFieldSuffix(field);
		
		// This one cant be done through normal prepared statements.
		// We need to change the columns used
		PreparedStatement statement;
		switch (order) {
		case ASCENDING:
			statement = con.prepareStatement(String.format("SELECT `p`.`player_id`, `p`.`name`, `p`.`displayname`, `%1$s` FROM `PlayerAttempts` AS `s` JOIN `Players` AS `p` ON (`p`.`player_id` = `s`.`player_id`) WHERE `minigame_id`=? ORDER BY `%1$s` ASC LIMIT %2$d,%3$d;", fieldName, offset, length));
			break;
		case DESCENDING:
			statement = con.prepareStatement(String.format("SELECT `p`.`player_id`, `p`.`name`, `p`.`displayname`, `%1$s` FROM `PlayerAttempts` AS `s` JOIN `Players` AS `p` ON (`p`.`player_id` = `s`.`player_id`) WHERE `minigame_id`=? ORDER BY `%1$s` DESC LIMIT %2$d,%3$d;", fieldName, offset, length));
			break;
		default:
			throw new AssertionError();
		}
		
		List<StoredStat> stats = Lists.newArrayList();
		statement.setInt(1, minigameId);
		ResultSet rs = statement.executeQuery();
		try {
			while (rs.next()) {
				stats.add(loadAttempt(rs, fieldName));
			}
			
			return stats;
		} finally {
			rs.close();
			statement.close();
		}
	}
	
	private StoredStat loadAttempt(ResultSet rs, String fieldName) throws SQLException {
		long value = rs.getLong(fieldName);
		
		UUID playerId = UUID.fromString(rs.getString("player_id"));
		String name = rs.getString("name");
		String displayName = rs.getString("displayname");
		
		return new StoredStat(playerId, name, displayName, value);
	}
	
	// Loads from the stats table
	private List<StoredStat> loadStats(int minigameId) throws SQLException {
		String statName = stat.getName() + stat.getFormat().getFieldSuffix(field);
		
		PreparedStatement statement;
		switch (order) {
		case ASCENDING:
			statement = database.getStatsAsc;
			break;
		case DESCENDING:
			statement = database.getStatsDesc;
			break;
		default:
			throw new AssertionError();
		}
		
		statement.setInt(1, minigameId);
		statement.setString(2, statName);
		
		statement.setInt(3, offset);
		statement.setInt(4, length);
		
		List<StoredStat> stats = Lists.newArrayList();
		ResultSet rs = statement.executeQuery();
		try {
			while (rs.next()) {
				stats.add(loadStat(rs));
			}
			
			return stats;
		} finally {
			rs.close();
		}
	}
	
	private StoredStat loadStat(ResultSet rs) throws SQLException {
		UUID playerId = UUID.fromString(rs.getString("player_id"));
		String name = rs.getString("name");
		String displayName = rs.getString("displayname");
		long value = rs.getLong("value");
		
		return new StoredStat(playerId, name, displayName, value);
	}
}
