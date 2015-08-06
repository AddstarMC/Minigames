package au.com.mineauz.minigames.backend.both;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import au.com.mineauz.minigames.backend.BackendImportCallback;
import au.com.mineauz.minigames.backend.ConnectionHandler;
import au.com.mineauz.minigames.backend.ConnectionPool;
import au.com.mineauz.minigames.backend.ExportNotifier;
import au.com.mineauz.minigames.backend.StatementKey;
import au.com.mineauz.minigames.stats.StatFormat;

public class SQLExport {
	private final ConnectionPool pool;
	private final BackendImportCallback callback;
	private final ExportNotifier notifier;
	
	private final StatementKey getPlayers;
	private final StatementKey getMinigames;
	private final StatementKey getStats;
	private final StatementKey getStatMetadata;
	
	private ConnectionHandler handler;
	
	private String notifyState;
	private int notifyCount;
	private long notifyTime;
	
	
	public SQLExport(ConnectionPool pool, BackendImportCallback callback, ExportNotifier notifier) {
		this.pool = pool;
		this.callback = callback;
		this.notifier = notifier;
		
		// Prepare queries
		getPlayers = new StatementKey("SELECT * FROM `Players`;");
		getMinigames = new StatementKey("SELECT * FROM `Minigames`;");
		getStats = new StatementKey("SELECT * FROM `PlayerStats`;");
		getStatMetadata = new StatementKey("SELECT * FROM `StatMetadata`;");
	}
	
	public void beginExport() {
		try {
			handler = pool.getConnection();
			callback.begin();
			
			exportPlayers();
			exportMinigames();
			exportStats();
			exportStatMetadata();
			
			notifyNext("Done");
			
			callback.end();
			notifier.onComplete();
		} catch (SQLException e) {
			notifier.onError(e, notifyState, notifyCount);
		} catch (IllegalStateException e) {
			notifier.onError(e, notifyState, notifyCount);
		} finally {
			handler.release();
		}
	}
	
	private void exportPlayers() throws SQLException {
		notifyNext("Exporting players...");
		ResultSet rs = handler.executeQuery(getPlayers);
		try {
			while (rs.next()) {
				callback.acceptPlayer(UUID.fromString(rs.getString("player_id")), rs.getString("name"), rs.getString("displayname"));
				++notifyCount;
				notifyProgress();
			}
		} finally {			
			rs.close();
		}
	}
	
	private void exportMinigames() throws SQLException {
		notifyNext("Exporting minigames...");
		ResultSet rs = handler.executeQuery(getMinigames);
		try {
			while (rs.next()) {
				callback.acceptMinigame(rs.getInt("minigame_id"), rs.getString("name"));
				++notifyCount;
				notifyProgress();
			}
		} finally {			
			rs.close();
		}
	}
	
	private void exportStats() throws SQLException {
		notifyNext("Exporting stats...");
		ResultSet rs = handler.executeQuery(getStats);
		try {
			while (rs.next()) {
				callback.acceptStat(UUID.fromString(rs.getString("player_id")), rs.getInt("minigame_id"), rs.getString("stat"), rs.getLong("value"));
				++notifyCount;
				notifyProgress();
			}
		} finally {			
			rs.close();
		}
	}
	
	private void exportStatMetadata() throws SQLException {
		notifyNext("Exporting metadata...");
		ResultSet rs = handler.executeQuery(getStatMetadata);
		try {
			while (rs.next()) {
				String rawFormat = rs.getString("format");
				StatFormat format = null;
				for (StatFormat f : StatFormat.values()) {
					if (f.name().equalsIgnoreCase(rawFormat)) {
						format = f;
						break;
					}
				}
				
				if (format == null) {
					continue;
				}
				
				callback.acceptStatMetadata(rs.getInt("minigame_id"), rs.getString("stat"), rs.getString("display_name"), format);
				++notifyCount;
				notifyProgress();
			}
		} finally {			
			rs.close();
		}
	}
	
	private void notifyProgress() {
		if (System.currentTimeMillis() - notifyTime >= 2000) {
			notifier.onProgress(notifyState, notifyCount);
			notifyTime = System.currentTimeMillis();
		}
	}
	
	private void notifyNext(String state) {
		if (notifyCount != 0) {
			notifier.onProgress(notifyState, notifyCount);
		}
		
		notifyTime = System.currentTimeMillis();
		notifyCount = 0;
		notifyState = state;
		
		notifier.onProgress(state, 0);
	}
}
