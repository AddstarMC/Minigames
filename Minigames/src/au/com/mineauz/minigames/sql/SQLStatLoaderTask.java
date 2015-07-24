package au.com.mineauz.minigames.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Maps;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredHistoryStats;

public class SQLStatLoaderTask implements Callable<Collection<StoredHistoryStats>> {
	private final Minigame minigame;
	private final Logger logger;
	private final SQLDatabase database;
	
	private Connection con;
	
	public SQLStatLoaderTask(Minigame minigame, Minigames plugin) {
		this.minigame = minigame;
		this.database = plugin.getSQL();
		this.logger = plugin.getLogger();
	}
	
	@Override
	public Collection<StoredHistoryStats> call() throws Exception {
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
	
	private Collection<StoredHistoryStats> loadMinigameData() {
		MinigameUtils.debugMessage("SQL begining stat load for " + minigame.getName(false));
		try {
			// First get the id
			int minigameId = SQLUtilities.getMinigameId(database.insertMinigame, minigame);
			
			Map<UUID, StoredHistoryStats> cache = Maps.newHashMap();
			loadAttempts(minigameId, cache);
			loadStats(minigameId, cache);
			
			return cache.values();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Failed to load stats for " + minigame.getName(false), e);
			return Collections.emptyList();
		} finally {
			MinigameUtils.debugMessage("SQL completed stat load for " + minigame.getName(false));
		}
	}
	
	private void loadAttempts(int minigameId, Map<UUID, StoredHistoryStats> cache) throws SQLException {
		database.getAttempts.setInt(1, minigameId);
		ResultSet rs = database.getAttempts.executeQuery();
		try {
			while (rs.next()) {
				loadAttempt(rs, cache);
			}
		} finally {
			rs.close();
		}
	}
	
	private void loadAttempt(ResultSet rs, Map<UUID, StoredHistoryStats> cache) throws SQLException {
		UUID playerId = UUID.fromString(rs.getString("player_id"));
		
		StoredHistoryStats stats = cache.get(playerId);
		if (stats == null) {
			stats = new StoredHistoryStats(playerId, rs.getString("name"), rs.getString("displayname"), minigame);
			cache.put(playerId, stats);
		}
		
		stats.addStat(MinigameStats.Wins, StatValueField.Total, rs.getLong("wins"));
		stats.addStat(MinigameStats.Attempts, StatValueField.Total, rs.getLong("attempts"));
		stats.addStat(MinigameStats.CompletionTime, StatValueField.Min, rs.getLong("time_min"));
		stats.addStat(MinigameStats.CompletionTime, StatValueField.Max, rs.getLong("time_max"));
		stats.addStat(MinigameStats.CompletionTime, StatValueField.Total, rs.getLong("time_total"));
	}
	
	private void loadStats(int minigameId, Map<UUID, StoredHistoryStats> cache) throws SQLException {
		database.getStats.setInt(1, minigameId);
		ResultSet rs = database.getStats.executeQuery();
		try {
			while (rs.next()) {
				loadStat(rs, cache);
			}
		} finally {
			rs.close();
		}
	}
	
	private void loadStat(ResultSet rs, Map<UUID, StoredHistoryStats> cache) throws SQLException {
		UUID playerId = UUID.fromString(rs.getString("player_id"));
		
		StoredHistoryStats stats = cache.get(playerId);
		if (stats == null) {
			return;
		}
		
		String rawStatId = rs.getString("stat");
		
		// Split out suffix and name
		String statName;
		String suffix;
		if (rawStatId.contains("_")) {
			int pos = rawStatId.indexOf("_");
			statName = rawStatId.substring(0, pos);
			suffix = rawStatId.substring(pos);
		} else {
			statName = rawStatId;
			suffix = "";
		}
		
		// Get stat and field
		MinigameStat stat = MinigameStats.getStat(statName);
		if (stat == null) {
			return;
		}
		
		StatValueField field = stat.getFormat().getFieldBySuffix(suffix);
		
		long value = rs.getLong("value");
		
		stats.addStat(stat, field, value);
	}
}
